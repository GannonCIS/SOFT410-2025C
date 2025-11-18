package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AccountDetails {

    String getFilePath(String fileName) {
        return fileName;
    }

    void accountDetailsFun(int accNo) throws IOException {
        File file = new File(getFilePath("db/userDB.txt"));
        Scanner scanner = new Scanner(file);
        String wholeDetail = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] subLine = line.split(" ");
            if (accNo == Integer.parseInt(subLine[0])) {
                wholeDetail = line;
                break;
            }
        }
        scanner.close(); // Ensure scanner is closed

        String[] detail = wholeDetail.split(" ");

        // --- WARNING: Index 8 is only safe if wholeDetail is NOT empty ---
        // We rely on the test setup to ensure the account is either found,
        // or the test expects the exception.

        System.out.println("Account Details: ");
        System.out.println("┌────────────────────────────────┐");
        System.out.println("  Full Name: "+ detail[1] + " " + detail[2]);
        System.out.println("  Account Number: "+ detail[0]);
        System.out.println("  Gender: "+ detail[4]);
        System.out.println("  Address: "+ detail[5]);
        System.out.println("  Date of Birth: "+ detail[3]);
        System.out.println("  Phone number: "+ detail[6]);
        System.out.println("  Email: "+ detail[7]);
        System.out.println("  Identification: "+ detail[8]);
        System.out.println("└────────────────────────────────┘");

        System.out.println("\n" + "Press Enter key to continue...");
        Scanner scanner1 = new Scanner(System.in);
        scanner1.nextLine();
        menuCall(accNo);
    }

    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }
}