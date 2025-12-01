package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AccountDetails implements UserRepository.PathResolver{

    private final UserRepository userRepository;

    public AccountDetails() {
        this.userRepository = new UserRepository(this);
    }

    protected AccountDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getFilePath(String fileName) {
        return fileName;
    }

    void accountDetailsFun(int accNo) throws IOException {
        Account account = userRepository.getAccountByAccNo(accNo);
        if (account == null) { // Check for null instead of empty line
            System.out.println("Error: Account details not found.");
            menuCall(accNo);
            return;
        }
        displayAccountDetails(account);
        handleContinuationFlow(account);
    }

    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }

    private void handleContinuationFlow(Account account) throws IOException {
        System.out.println();
        menuCall(account.getAccountNumber());
    }

    private void displayAccountDetails(Account account) {
        System.out.println("Account Details: ");
        System.out.println("┌────────────────────────────────┐");
        System.out.println("  Full Name: "+ account.getFirstName() + " " + account.getLastName());
        System.out.println("  Account Number: "+ account.getAccountNumber());
        System.out.println("  Gender: "+ account.getGender());
        System.out.println("  Address: "+ account.getAddress());
        System.out.println("  Date of Birth: "+ account.getDob());
        System.out.println("  Phone number: "+ account.getPhoneNumber());
        System.out.println("  Email: "+ account.getEmail());
        System.out.println("  Identification: "+ account.getCitizenshipNumber());
        System.out.println("└────────────────────────────────┘");
    }
}