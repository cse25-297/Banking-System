
package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseInitializer {

    public static void createTables() {

        String customersTable = """
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id TEXT PRIMARY KEY,
                    customer_type TEXT NOT NULL,
                    display_name TEXT NOT NULL,
                    first_name TEXT,
                    surname TEXT,
                    company_name TEXT,
                    registration_number TEXT,
                    contact_person TEXT,
                    address TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    email TEXT NOT NULL,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL
                )
                """;

        String accountsTable = """
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number TEXT PRIMARY KEY,
                    customer_id TEXT NOT NULL,
                    account_type TEXT NOT NULL,
                    branch TEXT NOT NULL,
                    balance REAL NOT NULL,
                    date_opened TEXT NOT NULL,
                    employer_name TEXT,
                    employer_address TEXT,
                    job_title TEXT,
                    status TEXT NOT NULL DEFAULT 'OPEN',
                    last_interest_date TEXT,
                    FOREIGN KEY (customer_id)
                        REFERENCES customers(customer_id)
                )
                """;

        String transactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id TEXT PRIMARY KEY,
                    account_number TEXT NOT NULL,
                    transaction_type TEXT NOT NULL,
                    amount REAL NOT NULL,
                    transaction_date TEXT NOT NULL,
                    balance_after REAL NOT NULL,
                    FOREIGN KEY (account_number)
                        REFERENCES accounts(account_number)
                )
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(customersTable);
            statement.execute(accountsTable);
            statement.execute(transactionsTable);

            upgradeCustomerTable(connection);
            upgradeAccountTable(connection);

            System.out.println(
                    "Database tables created successfully."
            );

        } catch (SQLException e) {
            System.out.println(
                    "Database initialization failed."
            );
            e.printStackTrace();
        }
    }

    private static void upgradeCustomerTable(Connection connection)
            throws SQLException {

        String[] columns = {
                "first_name TEXT",
                "surname TEXT",
                "company_name TEXT",
                "registration_number TEXT",
                "contact_person TEXT"
        };

        for (String column : columns) {
            String columnName = column.split(" ")[0];
            boolean exists = false;

            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "PRAGMA table_info(customers)")) {

                while (result.next()) {
                    if (result.getString("name")
                            .equalsIgnoreCase(columnName)) {
                        exists = true;
                        break;
                    }
                }
            }

            if (!exists) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(
                            "ALTER TABLE customers ADD COLUMN " + column
                    );
                }
            }
        }

        System.out.println("Customer table upgraded successfully.");
    }

    private static void upgradeAccountTable(Connection connection)
            throws SQLException {

        boolean exists = false;

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "PRAGMA table_info(accounts)")) {

            while (result.next()) {
                if (result.getString("name")
                        .equalsIgnoreCase("last_interest_date")) {
                    exists = true;
                    break;
                }
            }
        }

        if (!exists) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "ALTER TABLE accounts ADD COLUMN last_interest_date TEXT"
                );
            }
        }

        System.out.println("Account table upgraded successfully.");
    }
}
