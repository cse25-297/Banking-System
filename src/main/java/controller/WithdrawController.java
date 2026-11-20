
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
import model.Customer;
import model.AccountType;
import model.Transaction;

import java.io.IOException;
import java.sql.SQLException;

public class WithdrawController {

    @FXML private ComboBox<Account> accountComboBox;
    @FXML private TextField amountField;

    @FXML
    public void initialize() {

        Customer customer = BankSession.getCurrentCustomer();

        if (customer != null) {
            for (Account account : customer.getAccounts()) {
                if (account.getType() == AccountType.CHEQUE
                        || account.getType() == AccountType.INVESTMENT) {
                    accountComboBox.getItems().add(account);
                }
            }
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
    private void confirmWithdrawal() {

        Account account = accountComboBox.getValue();

        if (account == null || amountField.getText().isBlank()) {
            showAlert("Withdrawal Error",
                    "Please select an account and enter an amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());

            if (!Double.isFinite(amount) || amount <= 0) {
                showAlert("Withdrawal Error",
                        "Enter an amount greater than zero.");
                return;
            }

            double previousBalance = account.getBalance();
            int previousTransactionCount = account.getRecords().size();

            account.withdraw(amount);

            Transaction transaction =
                    account.getRecords().get(previousTransactionCount);

            try {
                AccountDAO accountDAO = new AccountDAO();

                accountDAO.updateAccountAndSaveTransaction(
                        account, transaction);

            } catch (SQLException e) {

                account.restoreBalance(previousBalance);
                account.removeLastTransaction();

                showAlert("Database Error",
                        "The withdrawal could not be saved. Please try again.");

                e.printStackTrace();
                return;
            }

            showAlert("Withdrawal Successful",
                    String.format(
                            "BWP %.2f withdrawn successfully.%nRemaining balance: BWP %.2f",
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
            showAlert("Withdrawal Error", "Please enter a valid amount.");

        } catch (UnsupportedOperationException | IllegalArgumentException e) {
            showAlert("Withdrawal Error", e.getMessage());
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
