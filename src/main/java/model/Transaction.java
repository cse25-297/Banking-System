package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String txnID;
    private TransactionType type;
    private double amount;
    private LocalDateTime timestamp;
    private double balanceAfter;

    public Transaction(String txnID, TransactionType type, double amount, double balanceAfter) {
        this.txnID = txnID;
        this.type = type;
        this.amount = Math.round(amount * 100.0) / 100.0;
        this.timestamp = LocalDateTime.now();
        this.balanceAfter = Math.round(balanceAfter * 100.0) / 100.0;


    }
    public Transaction(String txnID, TransactionType type,
                       double amount, LocalDateTime timestamp,
                       double balanceAfter) {

        this.txnID = txnID;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.balanceAfter = balanceAfter;
    }
    public String getTxnID() {
        return txnID;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %-10s | Amount: BWP %8.2f | Balance After: BWP %8.2f | ID: %s",
                timestamp.format(formatter), type, amount, balanceAfter, txnID);
    }
}