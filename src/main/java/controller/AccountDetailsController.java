
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import model.Account;

import java.time.format.DateTimeFormatter;

public class AccountDetailsController {

    @FXML
    private Label accountNumberLabel;

    @FXML
    private Label accountTypeLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label branchLabel;

    @FXML
    private Label dateOpenedLabel;

    @FXML
    public void initialize() {

        Account account = BankSession.getSelectedAccount();

        if (account != null) {
            displayAccountDetails(account);
        }
    }

    private void displayAccountDetails(Account account) {

        accountNumberLabel.setText(
                "Account Number: " + account.getAccountNumber()
        );

        accountTypeLabel.setText(
                "Account Type: " + account.getType()
        );

        balanceLabel.setText(
                String.format("Balance: BWP %.2f", account.getBalance())
        );

        branchLabel.setText(
                "Branch: " + account.getBranch()
        );

        dateOpenedLabel.setText(
                "Date Opened: " +
                        account.getDateOpened().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        )
        );
    }

    @FXML
    public void viewTransactionHistory() {

        if (BankSession.getSelectedAccount() == null) {
            showAlert("Error", "No account selected.");
            return;
        }

        try {
            Stage stage = (Stage) accountNumberLabel.getScene().getWindow();

            ScreenController.switchScreen(
                    "TransactionHistory.fxml", stage
            );

        } catch (Exception e) {
            showAlert("Error", "Could not open transaction history.");
        }
    }

    @FXML
    public void backToAccounts() {

        try {
            Stage stage = (Stage) accountNumberLabel.getScene().getWindow();

            ScreenController.switchScreen(
                    "ViewAccounts.fxml", stage
            );

        } catch (Exception e) {
            showAlert("Error", "Could not return to accounts.");
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
