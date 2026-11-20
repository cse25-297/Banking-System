
package controller;

import dao.AccountDAO;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import model.Account;
import model.AccountType;
import model.Customer;
import model.EmploymentDetails;
import model.Transaction;

import java.io.IOException;
import java.sql.SQLException;

public class OpenAccountController {

    @FXML private ComboBox<String> accountTypeComboBox;
    @FXML private TextField depositField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyAddressField;
    @FXML private TextField jobTitleField;

    @FXML
    public void initialize() {
        accountTypeComboBox.getItems().addAll(
                "Savings Account",
                "Investment Account",
                "Cheque Account"
        );
    }

    @FXML
    private void openAccount() {

        Customer customer = BankSession.getCurrentCustomer();

        if (customer == null) {
            showAlert("Account Error", "Please log in first.");
            return;
        }

        if (accountTypeComboBox.getValue() == null
                || depositField.getText().isBlank()) {
            showAlert("Account Error",
                    "Please select an account type and enter a deposit.");
            return;
        }

        Account account = null;

        try {
            double deposit = Double.parseDouble(depositField.getText());

            if (!Double.isFinite(deposit) || deposit <= 0) {
                showAlert("Account Error",
                        "Enter a valid deposit greater than zero.");
                return;
            }

            String selectedType = accountTypeComboBox.getValue();
            AccountType type;

            if (selectedType.equals("Savings Account")) {
                type = AccountType.SAVINGS;
            } else if (selectedType.equals("Investment Account")) {
                type = AccountType.INVESTMENT;
            } else {
                type = AccountType.CHEQUE;
            }

            EmploymentDetails employment = null;

            if (type == AccountType.CHEQUE) {

                String company = companyNameField.getText().trim();
                String address = companyAddressField.getText().trim();
                String job = jobTitleField.getText().trim();

                if (company.isEmpty() || address.isEmpty() || job.isEmpty()) {
                    showAlert("Employment Details Required",
                            "Enter company name, company address and job title.");
                    return;
                }

                employment = new EmploymentDetails(company, address, job);
            }

            if (type == AccountType.INVESTMENT && deposit < 500) {
                showAlert("Account Error",
                        "Investment Accounts require at least BWP 500.00.");
                return;
            }

            account = customer.openAccount(type, deposit, employment);

            AccountDAO accountDAO = new AccountDAO();

            if (account.getRecords().isEmpty()) {
                customer.getAccounts().remove(account);
                throw new IllegalStateException(
                        "Opening transaction is missing."
                );
            }

            Transaction openingTransaction = account.getRecords().get(0);

            try {
                accountDAO.saveAccountWithOpeningTransaction(
                        account,
                        openingTransaction
                );

            } catch (SQLException e) {
                customer.getAccounts().remove(account);

                showAlert("Database Error",
                        "The account could not be saved. Please try again.");

                e.printStackTrace();
                return;
            }

            showAlert("Account Opened",
                    "Your " + selectedType + " has been opened successfully.\n"
                            + "Account number: " + account.getAccountNumber());

            accountTypeComboBox.setValue(null);
            depositField.clear();
            companyNameField.clear();
            companyAddressField.clear();
            jobTitleField.clear();

        } catch (NumberFormatException e) {
            showAlert("Account Error", "Enter a valid numeric deposit.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert("Account Error", e.getMessage());
        }
    }

    @FXML
    private void backToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            ScreenController.switchScreen("Dashboard.fxml", stage);

        } catch (IOException e) {
            e.printStackTrace();
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
