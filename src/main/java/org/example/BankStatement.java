package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class BankStatement {

    // --- NEW METHOD 1: Interceptor for file paths ---
    String getFilePath(String fileName) {
        return fileName;
    }

    void bankStatementFun(int accNo) throws IOException {
        // --- FIX: Use getFilePath ---
        File file = new File(getFilePath("db/Bank Statement/acc_"+accNo+".txt"));
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            System.out.println("\n");
            System.out.println("                           | Bank Statement |");
            System.out.println("---------------------------------------------------------------------------");
            System.out.printf("%-21s | %-6s | %-6s | %-7s | %-10s | %-8s%n", "Description", "Type", "Amount", "Remarks", "Date", "Time");
            System.out.println("---------------------------------------------------------------------------");
            while (scanner.hasNextLine()) {
                String trWLine = scanner.nextLine();
                String[] trLine = trWLine.split(" ");
                String description = trLine[0] + " " + trLine[1] + " " + trLine[2];
                String type = trLine[3];
                String amount = "$" + trLine[4];
                String remarks = trLine[5];
                String date = trLine[6];
                String time = trLine[7];
                System.out.printf("%-21s | %-6s | %-6s | %-7s | %-10s | %-8s%n", description, type, amount, remarks, date, time);
            }
            System.out.println("---------------------------------------------------------------------------");
        } catch (FileNotFoundException e) {
            System.out.println("No Transaction found!");
            exit(accNo);
            return; // Ensure flow stops here on file error
        }
        exit(accNo);
    }

    void exit(int accNo) throws IOException {
        System.out.println("\n" + "Press Enter key to continue...");
        Scanner scanner1 = new Scanner(System.in);
        scanner1.nextLine();
        // --- FIX: Call the interceptor method ---
        menuCall(accNo);
    }

    // --- NEW METHOD 2: Interceptor for Main.menu() ---
    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }
}