
package model;

public class IndividualCustomer extends Customer {

    private String firstName;
    private String surname;

    // Constructor used during new customer registration
    public IndividualCustomer(String firstName, String surname,
                              String address, String phone,
                              String email, String username,
                              String passwordHash) {

        super(address, phone, email, username, passwordHash);

        this.firstName = firstName;
        this.surname = surname;
    }

    // Constructor used when loading an existing customer from the database
    public IndividualCustomer(String customerID,
                              String firstName, String surname,
                              String address, String phone,
                              String email, String username,
                              String passwordHash) {

        super(customerID, address, phone, email, username, passwordHash);

        this.firstName = firstName;
        this.surname = surname;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
