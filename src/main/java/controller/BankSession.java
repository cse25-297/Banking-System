
package controller;

import model.Bank;
import model.Customer;
import model.Account;

public class BankSession {

    private static final Bank bank = new Bank();

    private static Customer currentCustomer;

    private static Account selectedAccount;

    public static Bank getBank() {
        return bank;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public static void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    public static Account getSelectedAccount() {
        return selectedAccount;
    }

    public static void setSelectedAccount(Account account) {
        selectedAccount = account;
    }
}
