
package model;

import java.time.LocalDate;

public class SavingsAccount extends Account implements InterestBearer {

    private static final double MONTHLY_RATE = 0.0005;

    // New savings account
    public SavingsAccount(Customer owner, String branch) {
        super(owner, branch);
        this.type = AccountType.SAVINGS;
    }

    // New savings account with opening balance
    public SavingsAccount(Customer owner, String branch, double openingBalance) {
        super(owner, branch, openingBalance);
        this.type = AccountType.SAVINGS;
    }

    // Restore existing savings account from database
    public SavingsAccount(String accountNumber, Customer owner,
                          String branch, double balance,
                          LocalDate dateOpened) {

        super(accountNumber, owner, branch, balance, dateOpened);
        this.type = AccountType.SAVINGS;
    }

    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException(
                "Withdrawals are not allowed on a Savings Account."
        );
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
