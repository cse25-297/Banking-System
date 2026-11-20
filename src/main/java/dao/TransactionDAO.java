package dao;

import model.Transaction;
import model.TransactionType;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    public void saveTransaction(String accountNumber,
                                Transaction transaction)
            throws SQLException {

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

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transaction.getTxnID());
            statement.setString(2, accountNumber);
            statement.setString(3, transaction.getType().name());
            statement.setDouble(4, transaction.getAmount());
            statement.setString(5, transaction.getTimestamp().toString());
            statement.setDouble(6, transaction.getBalanceAfter());

            statement.executeUpdate();
        }
    }
    public List<Transaction> getTransactionsByAccount(String accountNumber)
            throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String sql = """
            SELECT *
            FROM transactions
            WHERE account_number = ?
            ORDER BY transaction_date ASC
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Transaction transaction = new Transaction(
                            result.getString("transaction_id"),
                            TransactionType.valueOf(
                                    result.getString("transaction_type")
                            ),
                            result.getDouble("amount"),
                            LocalDateTime.parse(
                                    result.getString("transaction_date")
                            ),
                            result.getDouble("balance_after")
                    );

                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }
}