
package model;

import dao.AccountDAO;
import java.sql.SQLException;
import java.time.LocalDate;

public class InterestService {

    private final AccountDAO accountDAO;

    public InterestService() {
        this.accountDAO = new AccountDAO();
    }

    public void processMonthlyInterest(Account account)
            throws SQLException {

        if (!(account instanceof InterestBearer)) {
            return;
        }

        LocalDate today = LocalDate.now();

        LocalDate lastInterestDate =
                accountDAO.getLastInterestDate(account.getAccountNumber());

        if (lastInterestDate == null) {
            lastInterestDate = account.getDateOpened();
        }

        LocalDate nextInterestDate = lastInterestDate.plusMonths(1);

        while (!nextInterestDate.isAfter(today)) {

            double rate;

            if (account instanceof SavingsAccount) {
                rate = 0.0005;
            } else if (account instanceof InvestmentAccount) {
                rate = 0.05;
            } else {
                return;
            }

            double interest = Math.round(
                    account.getBalance() * rate * 100.0
            ) / 100.0;

            double previousBalance = account.getBalance();

            if (interest > 0) {
                account.applyInterest(interest);
            }

            try {
                accountDAO.saveMonthlyInterest(
                        account,
                        nextInterestDate,
                        interest
                );
            } catch (SQLException e) {
                account.restoreBalance(previousBalance);

                if (interest > 0) {
                    account.removeLastTransaction();
                }

                throw e;
            }
            }

            nextInterestDate = nextInterestDate.plusMonths(1);
        }
    }

