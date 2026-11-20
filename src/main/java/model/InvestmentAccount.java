
package model;

import java.time.LocalDate;

public class InvestmentAccount extends Account implements InterestBearer {

    private static final double MONTHLY_RATE = 0.05;
    private static final double MIN_OPENING_BALANCE = 500.00;

    // Create a new investment account
    public InvestmentAccount(Customer owner, String branch, double openingBalance) {
        super(owner, branch, validateOpeningBalance(openingBalance));
        this.type = AccountType.INVESTMENT;
    }

    // Restore an existing investment account from database
    public InvestmentAccount(String accountNumber, Customer owner,
                             String branch, double balance,
                             LocalDate dateOpened) {

        super(accountNumber, owner, branch, balance, dateOpened);
        this.type = AccountType.INVESTMENT;
    }

    private static double validateOpeningBalance(double openingBalance) {

        if (!Double.isFinite(openingBalance)
                || openingBalance < MIN_OPENING_BALANCE) {

            throw new IllegalArgumentException(
                    String.format(
                            "Investment Account requires a minimum opening balance of BWP %.2f",
                            MIN_OPENING_BALANCE
                    )
            );
        }

        return openingBalance;
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
                    "Insufficient funds in Investment Account."
            );
        }

        this.balance = roundCurrency(this.balance - roundedAmount);

        logTransaction(TransactionType.WITHDRAWAL, roundedAmount);
    }

    @Override
    public void payInterest() {

        double interest = roundCurrency(this.balance * MONTHLY_RATE);

        if (interest > 0) {
            this.balance = roundCurrency(this.balance + interest);
            logTransaction(TransactionType.INTEREST, interest);
        }
    }
}
