
package controller;

import dao.AccountDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import model.Account;
import model.Customer;

import java.io.IOException;
import java.sql.SQLException;

public class ViewAccountsController {

    @FXML
    private TableView<Account> accountsTable;

    @FXML
    private TableColumn<Account, String> accountNumberColumn;

    @FXML
    private TableColumn<Account, String> accountTypeColumn;

    @FXML
    private TableColumn<Account, Double> balanceColumn;

    @FXML
    private TableColumn<Account, String> statusColumn;

    @FXML
    public void initialize() {

        accountNumberColumn.setCellValueFactory(
                new PropertyValueFactory<>("accountNumber")
        );

        accountTypeColumn.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );

        balanceColumn.setCellValueFactory(
                new PropertyValueFactory<>("balance")
        );

        statusColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty("Open")
        );

        loadAccounts();
    }

    private void loadAccounts() {

        Customer customer = BankSession.getCurrentCustomer();

        if (customer == null) {
            showAlert("Error", "No customer is currently logged in.");
            return;
        }

        ObservableList<Account> accounts =
                FXCollections.observableArrayList(customer.getAccounts());

        accountsTable.setItems(accounts);
    }

    @FXML
    public void closeSelectedAccount() {

        Account selectedAccount =
                accountsTable.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            showAlert("No Account Selected",
                    "Please select an account to close.");
            return;
        }

        Customer customer = BankSession.getCurrentCustomer();

        if (customer == null) {
            showAlert("Error", "No customer is currently logged in.");
            return;
        }

        try {

            boolean closed = customer.closeAccount(
                    selectedAccount.getAccountNumber()
            );

            if (closed) {

                try {
                    AccountDAO accountDAO = new AccountDAO();

                    accountDAO.closeAccount(
                            selectedAccount.getAccountNumber()
                    );

                    loadAccounts();

                    showAlert("Account Closed",
                            "The account was successfully closed.");

                } catch (SQLException e) {

                    // Restore the account in memory if database closing fails.
                    customer.addLoadedAccount(selectedAccount);
                    loadAccounts();

                    showAlert("Database Error",
                            "The account could not be closed in the database.");

                    e.printStackTrace();
                }

            } else {
                showAlert("Cannot Close Account",
                        "The account must have a zero balance before it can be closed.");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert("Account Error", e.getMessage());
        }
    }

    @FXML
    public void viewAccountDetails() {

        Account selectedAccount =
                accountsTable.getSelectionModel().getSelectedItem();

        if (selectedAccount == null) {
            showAlert("No Account Selected",
                    "Please select an account to view its details.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/AccountDetails.fxml")
            );

            Parent root = loader.load();

            BankSession.setSelectedAccount(selectedAccount);

            Stage stage = (Stage) accountsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Error",
                    "Could not open account details: " + e.getMessage());
        }
    }

    @FXML
    public void backToDashboard() {

        try {
            Stage stage = (Stage) accountsTable.getScene().getWindow();

            ScreenController.switchScreen("Dashboard.fxml", stage);

        } catch (IOException e) {
            showAlert("Error",
                    "Could not return to the dashboard.");
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
