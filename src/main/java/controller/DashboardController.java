package controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Customer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalBalanceLabel;


    @FXML
    public void initialize() {

        Customer customer = BankSession.getCurrentCustomer();

        if (customer != null) {

            welcomeLabel.setText(
                    "Welcome, " + customer.getDisplayName()
            );

            totalBalanceLabel.setText(
                    String.format(
                            "Total Balance: BWP %.2f",
                            customer.getTotalBalance()
                    )
            );
        }
    }

    private void navigate(ActionEvent event, String screen) {
        try {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            ScreenController.switchScreen(screen, stage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void viewAccounts(ActionEvent event) {
        navigate(event, "ViewAccounts.fxml");
    }

    @FXML
    private void openNewAccount(ActionEvent event) {
        navigate(event, "OpenAccount.fxml");
    }

    @FXML
    private void depositMoney(ActionEvent event) {
        navigate(event, "Deposit.fxml");
    }

    @FXML
    private void withdrawMoney(ActionEvent event) {
        navigate(event, "Withdraw.fxml");
    }

    @FXML
    private void transactionHistory(ActionEvent event) {
        navigate(event, "TransactionHistory.fxml");
    }

    @FXML
    private void logout(ActionEvent event) {
        BankSession.setCurrentCustomer(null);
        navigate(event, "Login.fxml");
    }
}
