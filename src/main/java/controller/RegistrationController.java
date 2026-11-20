
package controller;

import dao.CustomerDAO;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import model.Bank;
import model.Customer;
import model.IndividualCustomer;
import model.OrganisationCustomer;

import java.io.IOException;
import java.sql.SQLException;

public class RegistrationController {

    @FXML private TextField fullNameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> customerTypeComboBox;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        customerTypeComboBox.getItems().addAll(
                "Individual", "Organisation"
        );
    }

    @FXML
    private void register() {

        if (fullNameField.getText().trim().isEmpty()
                || addressField.getText().trim().isEmpty()
                || phoneField.getText().trim().isEmpty()
                || usernameField.getText().trim().isEmpty()
                || passwordField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()
                || customerTypeComboBox.getValue() == null) {

            showAlert("Missing Information",
                    "Please complete all registration fields.");
            return;
        }

        if (!emailField.getText().contains("@")
                || !emailField.getText().contains(".")) {

            showAlert("Invalid Email",
                    "Please enter a valid email address.");
            return;
        }

        if (passwordField.getText().length() < 8) {

            showAlert("Weak Password",
                    "Your password must contain at least 8 characters.");
            return;
        }

        String fullName = fullNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();
        String customerType = customerTypeComboBox.getValue();

        Bank bank = BankSession.getBank();

        if (bank.findCustomer(username) != null) {
            showAlert("Username Already Exists",
                    "This username is already taken.");
            return;
        }

        String hashedPassword =
                model.PasswordUtil.hashPassword(password);

        Customer customer;

        if (customerType.equals("Individual")) {

            String[] names = fullName.split("\\s+", 2);

            if (names.length < 2) {
                showAlert("Registration Error",
                        "Enter both your first name and surname.");
                return;
            }

            customer = new IndividualCustomer(
                    names[0],
                    names[1],
                    address,
                    phone,
                    email,
                    username,
                    hashedPassword
            );

        } else {

            customer = new OrganisationCustomer(
                    fullName,
                    "NOT-PROVIDED",
                    "NOT-PROVIDED",
                    address,
                    phone,
                    email,
                    username,
                    hashedPassword
            );
        }

        // Save customer to the database first.
        try {

            if (customerDAO.usernameExists(username)) {
                showAlert("Username Already Exists",
                        "This username is already taken.");
                return;
            }

            customerDAO.saveCustomer(customer);

            bank.addCustomer(customer);
            backToLogin();

        } catch (SQLException e) {

            showAlert("Database Error",
                    "Could not save your registration. "
                            + "Please try again.\n" + e.getMessage());

            e.printStackTrace();
        }
    }

    @FXML
    private void backToLogin() {

        try {
            Stage stage = (Stage) fullNameField.getScene().getWindow();

            ScreenController.switchScreen("Login.fxml", stage);

        } catch (IOException e) {
            showAlert("Navigation Error",
                    "Could not return to the login screen.");
        }
    }

    private void showAlert(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
