
package dao;

import model.Account;
import model.AccountType;
import model.Customer;
import model.SavingsAccount;
import model.InvestmentAccount;
import model.ChequeAccount;
import model.Transaction;
import model.TransactionType;
import java.time.LocalDateTime;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // Save an account

    public void saveAccount(Account account) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            insertAccount(connection, account);
        }
    }

    public void saveAccountWithOpeningTransaction(
            Account account, Transaction transaction) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                insertAccount(connection, account);

                String sql = """
                INSERT INTO transactions (
                    transaction_id,
                    account_number,
                    transaction_type,
                    amount,
                    transaction_date,
                    balance_after
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, transaction.getTxnID());
                    statement.setString(2, account.getAccountNumber());
                    statement.setString(3, transaction.getType().name());
                    statement.setDouble(4, transaction.getAmount());
                    statement.setString(5, transaction.getTimestamp().toString());
                    statement.setDouble(6, transaction.getBalanceAfter());

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void insertAccount(Connection connection, Account account)
            throws SQLException {

        String sql = """
        INSERT INTO accounts (
            account_number,
            customer_id,
            account_type,
            branch,
            balance,
            date_opened,
            employer_name,
            employer_address,
            job_title,
            status
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'OPEN')
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, account.getAccountNumber());
            statement.setString(2, account.getOwner().getCustomerID());
            statement.setString(3, account.getType().name());
            statement.setString(4, account.getBranch());
            statement.setDouble(5, account.getBalance());
            statement.setString(6, account.getDateOpened().toString());

            if (account instanceof ChequeAccount cheque) {
                statement.setString(7, cheque.getEmployerName());
                statement.setString(8, cheque.getEmployerAddress());

                if (account.getOwner().getEmploymentDetails() != null) {
                    statement.setString(
                            9,
                            account.getOwner().getEmploymentDetails().getJobTitle()
                    );
                } else {
                    statement.setNull(9, java.sql.Types.VARCHAR);
                }

            } else {
                statement.setNull(7, java.sql.Types.VARCHAR);
                statement.setNull(8, java.sql.Types.VARCHAR);
                statement.setNull(9, java.sql.Types.VARCHAR);
            }

            statement.executeUpdate();
        }
    }


    // Retrieve all open accounts belonging to a customer
    public List<Account> getAccountsByCustomer(Customer customer)
            throws SQLException {

        List<Account> accounts = new ArrayList<>();

        String sql = """
                SELECT *
                FROM accounts
                WHERE customer_id = ?
                AND status = 'OPEN'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getCustomerID());

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    String accountNumber =
                            result.getString("account_number");

                    String branch = result.getString("branch");

                    double balance =
                            result.getDouble("balance");

                    LocalDate dateOpened =
                            LocalDate.parse(result.getString("date_opened"));

                    AccountType type =
                            AccountType.valueOf(result.getString("account_type"));

                    Account account;

                    switch (type) {

                        case SAVINGS:
                            account = new SavingsAccount(
                                    accountNumber,
                                    customer,
                                    branch,
                                    balance,
                                    dateOpened
                            );
                            break;

                        case INVESTMENT:
                            account = new InvestmentAccount(
                                    accountNumber,
                                    customer,
                                    branch,
                                    balance,
                                    dateOpened
                            );
                            break;

                        case CHEQUE:
                            account = new ChequeAccount(
                                    accountNumber,
                                    customer,
                                    branch,
                                    balance,
                                    dateOpened,
                                    result.getString("employer_name"),
                                    result.getString("employer_address")
                            );
                            break;

                        default:
                            continue;
                    }

                    TransactionDAO transactionDAO = new TransactionDAO();

                    List<model.Transaction> transactions =
                            transactionDAO.getTransactionsByAccount(accountNumber);

                    for (model.Transaction transaction : transactions) {
                        account.addLoadedTransaction(transaction);
                    }

                    accounts.add(account);
                }
            }
        }

        return accounts;
    }
    public void updateAccount(Account account) throws SQLException {

        String sql = """
            UPDATE accounts
            SET balance = ?
            WHERE account_number = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, account.getBalance());
            statement.setString(2, account.getAccountNumber());

            statement.executeUpdate();
        }
    }
    public void updateAccountAndSaveTransaction(
            Account account, model.Transaction transaction)
            throws SQLException {

        String updateSql = """
            UPDATE accounts
            SET balance = ?
            WHERE account_number = ?
            """;

        String insertSql = """
            INSERT INTO transactions (
                transaction_id,
                account_number,
                transaction_type,
                amount,
                transaction_date,
                balance_after
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement updateStatement =
                             connection.prepareStatement(updateSql)) {

                    updateStatement.setDouble(1, account.getBalance());
                    updateStatement.setString(2, account.getAccountNumber());

                    int updated = updateStatement.executeUpdate();

                    if (updated != 1) {
                        throw new SQLException("Account was not found.");
                    }
                }

                try (PreparedStatement insertStatement =
                             connection.prepareStatement(insertSql)) {

                    insertStatement.setString(1, transaction.getTxnID());
                    insertStatement.setString(2, account.getAccountNumber());
                    insertStatement.setString(3, transaction.getType().name());
                    insertStatement.setDouble(4, transaction.getAmount());
                    insertStatement.setString(
                            5, transaction.getTimestamp().toString());
                    insertStatement.setDouble(6, transaction.getBalanceAfter());

                    insertStatement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void closeAccount(String accountNumber) throws SQLException {

        String sql = """
            UPDATE accounts
            SET status = 'CLOSED'
            WHERE account_number = ?
            AND balance = 0
            AND status = 'OPEN'
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            int updated = statement.executeUpdate();

            if (updated != 1) {
                throw new SQLException(
                        "Account could not be closed. Check its balance and status."
                );
            }
        }
    }



    public void saveMonthlyInterest(
            Account account,
            LocalDate interestDate,
            double interestAmount
    ) throws SQLException {

        String updateSql = """
        UPDATE accounts
        SET balance = ?,
            last_interest_date = ?
        WHERE account_number = ?
        AND status = 'OPEN'
        """;

        String insertSql = """
        INSERT INTO transactions (
            transaction_id,
            account_number,
            transaction_type,
            amount,
            transaction_date,
            balance_after
        )
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        double previousBalance = account.getBalance();
        model.Transaction interestTransaction = null;

        if (interestAmount > 0) {
            interestTransaction =
                    account.getRecords().get(account.getRecords().size() - 1);
        }

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement updateStatement =
                             connection.prepareStatement(updateSql)) {

                    updateStatement.setDouble(1, account.getBalance());
                    updateStatement.setString(2, interestDate.toString());
                    updateStatement.setString(3, account.getAccountNumber());

                    if (updateStatement.executeUpdate() != 1) {
                        throw new SQLException("Account was not updated.");
                    }
                }

                if (interestTransaction != null) {

                    try (PreparedStatement insertStatement =
                                 connection.prepareStatement(insertSql)) {

                        insertStatement.setString(1, interestTransaction.getTxnID());
                        insertStatement.setString(2, account.getAccountNumber());
                        insertStatement.setString(3, interestTransaction.getType().name());
                        insertStatement.setDouble(4, interestTransaction.getAmount());

                        insertStatement.setString(
                                5, interestTransaction.getTimestamp().toString()
                        );

                        insertStatement.setDouble(
                                6, interestTransaction.getBalanceAfter()
                        );

                        insertStatement.executeUpdate();
                    }
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
    public LocalDate getLastInterestDate(String accountNumber)
            throws SQLException {

        String sql = """
        SELECT last_interest_date
        FROM accounts
        WHERE account_number = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    String date = result.getString("last_interest_date");

                    if (date != null) {
                        return LocalDate.parse(date);
                    }
                }
            }
        }

        return null;
    }
}
