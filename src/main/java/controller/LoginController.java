
package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import model.Customer;
import model.PasswordUtil;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void login(ActionEvent event) {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error",
                    "Please enter your username and password.");
            return;
        }

        Customer customer =
                BankSession.getBank().findCustomer(username);

        if (customer == null ||
                !PasswordUtil.verifyPassword(
                        password, customer.getPasswordHash())) {

            showAlert("Login Failed",
                    "Incorrect username or password.");
            return;
        }

        BankSession.setCurrentCustomer(customer);

        try {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            ScreenController.switchScreen("Dashboard.fxml", stage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error",
                    "Could not open the dashboard.");
        }
    }

    @FXML
    private void openRegistration(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            ScreenController.switchScreen("Register.fxml", stage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
