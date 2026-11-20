
package controller;

import dao.AccountDAO;
import dao.TransactionDAO;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import model.Account;
import model.Customer;
import model.Transaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DepositController {

    @FXML private ComboBox<Account> accountComboBox;
    @FXML private TextField amountField;

    @FXML
    public void initialize() {
        Customer customer = BankSession.getCurrentCustomer();

        if (customer != null) {
            accountComboBox.getItems().setAll(customer.getAccounts());
        }

        accountComboBox.setCellFactory(list ->
                new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(Account account, boolean empty) {
                        super.updateItem(account, empty);
                        setText(empty || account == null ? null :
                                account.getType() + " - " + account.getAccountNumber());
                    }
                });

        accountComboBox.setButtonCell(
                new javafx.scene.control.ListCell<>() {
                    @Override
                    protected void updateItem(Account account, boolean empty) {
                        super.updateItem(account, empty);
                        setText(empty || account == null ? null :
                                account.getType() + " - " + account.getAccountNumber());
                    }
                });
    }

    @FXML
    private void confirmDeposit() {

        Account account = accountComboBox.getValue();

        if (account == null || amountField.getText().isBlank()) {
            showAlert("Deposit Error",
                    "Please select an account and enter an amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());

            if (!Double.isFinite(amount) || amount <= 0) {
                showAlert("Deposit Error",
                        "Enter an amount greater than zero.");
                return;
            }

            double previousBalance = account.getBalance();

            int previousTransactionCount = account.getRecords().size();

            account.deposit(amount);

            List<Transaction> records = account.getRecords();
            Transaction transaction =
                    records.get(previousTransactionCount);

            try {
                AccountDAO accountDAO = new AccountDAO();

                accountDAO.updateAccountAndSaveTransaction(account, transaction);

            } catch (SQLException e) {

                account.restoreBalance(previousBalance);

                account.removeLastTransaction();

                showAlert("Database Error",
                        "The deposit could not be saved. Please try again.");

                e.printStackTrace();
                return;
            }

            showAlert("Deposit Successful",
                    String.format(
                            "BWP %.2f deposited successfully.%nNew balance: BWP %.2f",
                            amount, account.getBalance()));

            try {
                Stage stage = (Stage) amountField.getScene().getWindow();
                ScreenController.switchScreen("Dashboard.fxml", stage);
            } catch (Exception e) {
                showAlert("Error", "Could not return to dashboard.");
            }

            amountField.clear();
            accountComboBox.setValue(null);

        } catch (NumberFormatException e) {
            showAlert("Deposit Error", "Please enter a valid amount.");
        } catch (IllegalArgumentException e) {
            showAlert("Deposit Error", e.getMessage());
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
