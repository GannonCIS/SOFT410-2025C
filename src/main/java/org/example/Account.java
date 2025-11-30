package org.example;

public class Account {
    private final int accountNumber;
    private final String firstName;
    private final String lastName;
    private final String dob;
    private final String gender;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final String citizenshipNumber;
    private final String password;

    public Account(int accountNumber, String firstName, String lastName, String dob, String gender, String address, String phoneNumber, String email, String citizenshipNumber, String password) {
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.citizenshipNumber = citizenshipNumber;
        this.password = password;
    }

    public int getAccountNumber() { return accountNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getCitizenshipNumber() { return citizenshipNumber; }
    public String getPassword() { return password; }

    public String toUserDBLine() {
        return String.format("%d %s %s %s %s %s %s %s %s",
                accountNumber, firstName, lastName, dob, gender, address, phoneNumber, email, citizenshipNumber);
    }
}