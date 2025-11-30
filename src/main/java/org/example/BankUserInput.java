package org.example;

import java.util.Scanner;

// Dedicated class for handling user input specific to bank operations.
public class BankUserInput {
    public TransactionDetails getTransferDetails(int senderAccNo) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Receiver's Account Number: ");
        int rAccNo = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Amount: ");
        int tAmount = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Remarks: ");
        String tRemarks = scanner.nextLine();
        System.out.println("\n");

        return new TransactionDetails(senderAccNo, rAccNo, tAmount, tRemarks);
    }

    public String[] getNewAccountInfo() {
        String[] accLineInfo = new String[9];
        Scanner scanner = new Scanner(System.in);
        String fullName = "";
        String[] fullNameArr;

        while (true) {
            System.out.println("Enter your Name: ");
            fullName = scanner.nextLine();
            fullNameArr = fullName.split(" ");

            if (fullNameArr.length == 2) {
                accLineInfo[0] = fullNameArr[0]; // First Name
                accLineInfo[1] = fullNameArr[1]; // Last Name
                break; // Exit loop on valid input
            } else {
                System.out.println("Please provide both first name and last name.");
            }
        }

        System.out.println("Enter your Date of Birth (YYYY-MM-DD): ");
        accLineInfo[2] = scanner.nextLine();
        System.out.println("Enter your Gender: ");
        accLineInfo[3] = scanner.nextLine();
        System.out.println("Enter your Address: ");
        accLineInfo[4] = scanner.nextLine();
        System.out.println("Enter your Phone Number: ");
        accLineInfo[5] = scanner.nextLine();
        System.out.println("Enter your Email: ");
        accLineInfo[6] = scanner.nextLine();
        System.out.println("Enter your Citizenship Number: ");
        accLineInfo[7] = scanner.nextLine();
        System.out.println("Create a Password for your Account: ");
        accLineInfo[8] = scanner.nextLine();
        return accLineInfo;
    }
}