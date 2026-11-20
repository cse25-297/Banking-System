package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import model.Account;
import model.Customer;
import model.Transaction;

import java.time.format.DateTimeFormatter;

public class TransactionHistoryController {

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, String> amountColumn;

    @FXML
    private TableColumn<Transaction, String> balanceColumn;

    @FXML
    private TableColumn<Transaction, String> transactionIDColumn;

    @FXML
    public void initialize() {

        dateColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getTimestamp().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        )
                )
        );

        typeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getType().toString()
                )
        );

        amountColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        String.format("BWP %.2f", cell.getValue().getAmount())
                )
        );

        balanceColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        String.format("BWP %.2f", cell.getValue().getBalanceAfter())
                )
        );

        transactionIDColumn.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getTxnID()
                )
        );

        loadTransactions();
    }


    private void loadTransactions() {

        Account selectedAccount = BankSession.getSelectedAccount();

        if (selectedAccount == null) {
            return;
        }

        ObservableList<Transaction> transactions =
                FXCollections.observableArrayList(
                        selectedAccount.getRecords()
                );

        transactionTable.setItems(transactions);
    }


    @FXML
    public void backToDashboard() {

        try {
            Stage stage = (Stage) transactionTable.getScene().getWindow();

            ScreenController.switchScreen("Dashboard.fxml", stage);

        } catch (Exception e) {
            showAlert("Error", "Could not return to the dashboard.");
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
