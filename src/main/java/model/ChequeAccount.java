
package model;

import java.time.LocalDate;

public class ChequeAccount extends Account {

    private String employerName;
    private String employerAddress;

    // Create a new cheque account
    public ChequeAccount(Customer owner, String branch,
                         String employerName, String employerAddress) {

        super(owner, branch);

        this.type = AccountType.CHEQUE;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    // Create a new cheque account with opening balance
    public ChequeAccount(Customer owner, String branch,
                         double openingBalance,
                         String employerName, String employerAddress) {

        super(owner, branch, openingBalance);

        this.type = AccountType.CHEQUE;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    // Restore an existing cheque account from database
    public ChequeAccount(String accountNumber, Customer owner,
                         String branch, double balance,
                         LocalDate dateOpened,
                         String employerName, String employerAddress) {

        super(accountNumber, owner, branch, balance, dateOpened);

        this.type = AccountType.CHEQUE;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public void withdraw(double amount) {

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero."
            );
        }

        double roundedAmount = roundCurrency(amount);

        if (roundedAmount > this.balance) {
            throw new IllegalArgumentException(
                    "Insufficient funds in Cheque Account."
            );
        }

        this.balance = roundCurrency(this.balance - roundedAmount);

        logTransaction(TransactionType.WITHDRAWAL, roundedAmount);
    }

    public String getEmployerDetails() {
        return employerName + " - " + employerAddress;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }
}
