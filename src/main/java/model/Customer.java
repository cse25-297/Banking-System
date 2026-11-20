package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Customer {
    private String customerID;
    private String address;
    private String phone;
    private String email;
    private String username;
    private String passwordHash;

    protected List<Account> accounts;
    protected EmploymentDetails employmentDetails;

    public Customer(String address, String phone, String email,
                    String username, String passwordHash) {

        this("CUST-" + UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase(),
                address, phone, email, username, passwordHash);
    }

    protected Customer(String customerID, String address, String phone,
                       String email, String username, String passwordHash) {

        this.customerID = customerID;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.accounts = new ArrayList<>();
    }

    public abstract String getDisplayName();

    public Account openAccount(AccountType type) {
        Account account;
        switch (type) {
            case SAVINGS:
                account = new SavingsAccount(this, "Main Branch");
                break;
            case INVESTMENT:
                throw new IllegalArgumentException(
                        "Investment Account requires an opening deposit of at least BWP 500.00."
                );
            case CHEQUE:
                throw new IllegalArgumentException(
                        "Cheque Account requires employment details."
                );
            default:
                throw new IllegalArgumentException("Unsupported account type: " + type);
        }
        accounts.add(account);
        return account;
    }

    public Account openAccount(AccountType type, double deposit, EmploymentDetails emp) {
        if (emp != null) {
            this.employmentDetails = emp;
        }

        Account account;
        switch (type) {
            case SAVINGS:
                account = new SavingsAccount(this, "Main Branch", deposit);
                break;
            case INVESTMENT:
                account = new InvestmentAccount(this, "Main Branch", deposit);
                break;
            case CHEQUE:
                if (this.employmentDetails == null
                        || this.employmentDetails.getCompanyName() == null
                        || this.employmentDetails.getCompanyName().isBlank()) {
                    throw new IllegalArgumentException(
                            "Valid employment details are required to open a Cheque Account."
                    );
                }
                account = new ChequeAccount(
                        this,
                        "Main Branch",
                        deposit,
                        this.employmentDetails.getCompanyName(),
                        this.employmentDetails.getCompanyAddress()
                );
                break;
            default:
                throw new IllegalArgumentException("Unsupported account type: " + type);
        }
        accounts.add(account);
        return account;
    }

    public boolean closeAccount(String accNumber) {
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            if (acc.getAccountNumber().equals(accNumber)) {
                if (acc.getBalance() != 0.0) {
                    throw new IllegalStateException(
                            String.format("Cannot close account %s: balance must be BWP 0.00 (Current: BWP %.2f)",
                                    accNumber, acc.getBalance())
                    );
                }
                accounts.remove(i);
                return true;
            }
        }
        return false;
    }

    public double getTotalBalance() {
        double total = 0.0;
        for (Account acc : accounts) {
            total += acc.getBalance();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public EmploymentDetails getEmploymentDetails() {
        return employmentDetails;
    }

    public void setEmploymentDetails(EmploymentDetails employmentDetails) {
        this.employmentDetails = employmentDetails;
    }
    public void addLoadedAccount(Account account) {
        if (account != null) {
            this.accounts.add(account);
        }
    }
}