package dao;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Customer;
import model.IndividualCustomer;
import model.OrganisationCustomer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDAO {

    public void saveCustomer(Customer customer) throws SQLException {

        String sql = """
                INSERT INTO customers (
                    customer_id,
                    customer_type,
                    display_name,
                    first_name,
                    surname,
                    company_name,
                    registration_number,
                    contact_person,
                    address,
                    phone,
                    email,
                    username,
                    password_hash
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, customer.getCustomerID());
            statement.setString(3, customer.getDisplayName());

            if (customer instanceof IndividualCustomer individual) {

                statement.setString(2, "INDIVIDUAL");
                statement.setString(4, individual.getFirstName());
                statement.setString(5, individual.getSurname());

                statement.setNull(6, java.sql.Types.VARCHAR);
                statement.setNull(7, java.sql.Types.VARCHAR);
                statement.setNull(8, java.sql.Types.VARCHAR);

            } else if (customer instanceof OrganisationCustomer organisation) {

                statement.setString(2, "ORGANISATION");

                statement.setNull(4, java.sql.Types.VARCHAR);
                statement.setNull(5, java.sql.Types.VARCHAR);

                statement.setString(6, organisation.getCompanyName());
                statement.setString(7, organisation.getRegistrationNumber());
                statement.setString(8, organisation.getContactPerson());

            } else {
                throw new IllegalArgumentException(
                        "Unsupported customer type."
                );
            }

            statement.setString(9, customer.getAddress());
            statement.setString(10, customer.getPhone());
            statement.setString(11, customer.getEmail());
            statement.setString(12, customer.getUsername());
            statement.setString(13, customer.getPasswordHash());

            statement.executeUpdate();

            System.out.println("Customer saved successfully.");
        }
    }

    public boolean usernameExists(String username) throws SQLException {

        String sql = "SELECT username FROM customers WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery())  {
                return result.next();
            }
        }
    }
    public List<Customer> getAllCustomers() throws SQLException {

        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customers";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                String customerID = result.getString("customer_id");
                String customerType = result.getString("customer_type");

                String address = result.getString("address");
                String phone = result.getString("phone");
                String email = result.getString("email");
                String username = result.getString("username");
                String passwordHash = result.getString("password_hash");

                Customer customer;

                if (customerType.equals("INDIVIDUAL")) {

                    customer = new IndividualCustomer(
                            customerID,
                            result.getString("first_name"),
                            result.getString("surname"),
                            address,
                            phone,
                            email,
                            username,
                            passwordHash
                    );

                } else if (customerType.equals("ORGANISATION")) {

                    customer = new OrganisationCustomer(
                            customerID,
                            result.getString("company_name"),
                            result.getString("registration_number"),
                            result.getString("contact_person"),
                            address,
                            phone,
                            email,
                            username,
                            passwordHash
                    );

                } else {
                    continue;
                }

                customers.add(customer);
            }
        }

        return customers;
    }

    public void updatePassword(String username, String newPasswordHash)
            throws SQLException {

        String sql = """
            UPDATE customers
            SET password_hash = ?
            WHERE username = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newPasswordHash);
            statement.setString(2, username);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Customer username not found.");
            }
        }
    }


}
