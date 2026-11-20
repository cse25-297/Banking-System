
package model;

public class OrganisationCustomer extends Customer {

    private String companyName;
    private String registrationNumber;
    private String contactPerson;

    // Constructor for new organisation registration
    public OrganisationCustomer(String companyName,
                                String registrationNumber,
                                String contactPerson,
                                String address, String phone,
                                String email, String username,
                                String passwordHash) {

        super(address, phone, email, username, passwordHash);

        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;
    }

    // Constructor for loading an existing organisation from the database
    public OrganisationCustomer(String customerID,
                                String companyName,
                                String registrationNumber,
                                String contactPerson,
                                String address, String phone,
                                String email, String username,
                                String passwordHash) {

        super(customerID, address, phone, email, username, passwordHash);

        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;
    }

    @Override
    public String getDisplayName() {
        return companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}
