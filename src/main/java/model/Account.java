
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Account {

    private String accountNumber;
    protected double balance;
    protected String branch;
    protected LocalDate dateOpened;
    protected AccountType type;

    protected Customer owner;
    protected List<Transaction> records;

    // Constructor for creating a new account
    public Account(Customer owner, String branch) {

        this.accountNumber = "ACC-" +
                UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase();

        this.owner = owner;
        this.branch = branch;
        this.balance = 0.0;
        this.dateOpened = LocalDate.now();
        this.records = new ArrayList<>();
    }

    // Constructor for creating an account with an opening balance
    public Account(Customer owner, String branch, double openingBalance) {

        this(owner, branch);

        if (!Double.isFinite(openingBalance) || openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative or invalid."
            );
        }

        double roundedOpening = roundCurrency(openingBalance);
        this.balance = roundedOpening;

        if (roundedOpening > 0) {
            logTransaction(TransactionType.DEPOSIT, roundedOpening);
        }
    }

    // Constructor for restoring an existing account from the database
    protected Account(String accountNumber, Customer owner,
                      String branch, double balance,
                      LocalDate dateOpened) {

        this.accountNumber = accountNumber;
        this.owner = owner;
        this.branch = branch;
        this.balance = roundCurrency(balance);
        this.dateOpened = dateOpened;
        this.records = new ArrayList<>();
    }

    public void deposit(double amount) {

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero."
            );
        }

        double roundedAmount = roundCurrency(amount);
        this.balance = roundCurrency(this.balance + roundedAmount);

        logTransaction(TransactionType.DEPOSIT, roundedAmount);
    }

    public void deposit(double amount, String reference) {

        deposit(amount);
    }

    public abstract void withdraw(double amount);

    public double getBalance() {
        return roundCurrency(this.balance);
    }

    protected double roundCurrency(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    protected void logTransaction(TransactionType type, double amount) {

        String txnId = "TXN-" +
                UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase();

        Transaction txn = new Transaction(
                txnId,
                type,
                roundCurrency(amount),
                roundCurrency(this.balance)
        );

        this.records.add(txn);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBranch() {
        return branch;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public AccountType getType() {
        return type;
    }

    public Customer getOwner() {
        return owner;
    }

    public List<Transaction> getRecords() {
        return records;
    }
    public void addLoadedTransaction(Transaction transaction) {
        if (transaction != null) {
            this.records.add(transaction);
        }
    }
    public void removeLastTransaction() {
        if (!records.isEmpty()) {
            records.remove(records.size() - 1);
        }
    }

  public void restoreBalance(double previousBalance) {
        this.balance = roundCurrency(previousBalance);
    }
    public void applyInterest(double interest) {

        if (!Double.isFinite(interest) || interest <= 0) {
            throw new IllegalArgumentException(
                    "Interest must be greater than zero."
            );
        }

        double roundedInterest = roundCurrency(interest);

        this.balance = roundCurrency(this.balance + roundedInterest);

        logTransaction(TransactionType.INTEREST, roundedInterest);
    }
}
