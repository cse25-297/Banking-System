package app;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.InterestService;
import model.Customer;
import controller.BankSession;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Create database tables
        DatabaseInitializer.createTables();

        // Load saved customers from the database
        CustomerDAO customerDAO = new CustomerDAO();

        List<Customer> customers = customerDAO.getAllCustomers();

        AccountDAO accountDAO = new AccountDAO();
        InterestService interestService = new InterestService();

        for (Customer customer : customers) {
            BankSession.getBank().addCustomer(customer);

            List<model.Account> accounts =
                    accountDAO.getAccountsByCustomer(customer);
            for (model.Account account : accounts) {
                interestService.processMonthlyInterest(account);
                customer.addLoadedAccount(account);
            }
        }

        System.out.println(
                "Loaded " + customers.size() + " customers from database."
        );

        // Open login screen
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/Login.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Banking System");
        stage.setScene(scene);
        stage.setWidth(900);
        stage.setHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
