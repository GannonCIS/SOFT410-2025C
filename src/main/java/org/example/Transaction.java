package org.example;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Transaction {

    // --- NEW METHOD: Interceptor for file paths ---
    // This allows the test to override the base path ("db/") with the temp folder path.
    String getFilePath(String fileName) {
        return fileName;
    }

    void transactionFun(int accNo) throws IOException {
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
        allTransaction(accNo, rAccNo, tAmount, tRemarks);
    }

    void allTransaction(int accNo, int rAccNo, int tAmount, String tRemarks) throws IOException {
        if (rAccCheck(rAccNo)) {
            //rAcc Validated
            if (sAccBalCheck(accNo, tAmount)) {
                //sBalance ok
                transaction(accNo, rAccNo, tAmount);  //actual transaction
                writeTransaction(accNo, rAccNo, tAmount, tRemarks); //write transaction to file
                System.out.println("Transaction Successful!");
                System.out.println("Press any key to continue...");

                // --- FIX: Safely consume the required input token, if present ---
                try (Scanner tscanner = new Scanner(System.in)) {
                    if (tscanner.hasNextLine()) {
                        tscanner.nextLine();
                    }
                }

                // --- FIX 1: Call the interceptor method ---
                menuCall(accNo);
            } else {
                System.out.println("Insufficient Balance!");
            }
        } else {
            System.out.println("Incorrect Account Number!");
        }
    }

    // --- NEW METHOD: Interceptor for Main.menu() ---
    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }

    boolean rAccCheck(int rAccNo) throws FileNotFoundException {
        // --- FIX 2: Use getFilePath ---
        File file = new File(getFilePath("db/balanceDB.txt"));
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] subLine = line.split(" ");
            int a = Integer.parseInt(subLine[0]);
            if (rAccNo == a) return true;
        }
        return false;
    }

    boolean sAccBalCheck(int accNo, int tAmount) throws FileNotFoundException {
        // --- FIX 3: Use getFilePath ---
        File file = new File(getFilePath("db/balanceDB.txt"));
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] subLine = line.split(" ");
            int a = Integer.parseInt(subLine[0]);
            int b = Integer.parseInt(subLine[1]);
            if (accNo == a) {
                if (tAmount <= b) return true;
            }
        }
        return false;
    }

    void transaction(int accNo, int rAccNo, int tAmount) throws IOException {
        // --- FIX 4: Use getFilePath ---
        File file = new File(getFilePath("db/balanceDB.txt"));
        Scanner scanner = new Scanner(file);
        String newInfo = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] subLine = line.split(" ");
            int a = Integer.parseInt(subLine[0]);
            int b = Integer.parseInt(subLine[1]);
            if (accNo == a) {
                b = b - tAmount;
            } else if (rAccNo == a) {
                b = b + tAmount;
            }
            String newLine = a + " " + b;
            newInfo += newLine + "\n";
        }
        // --- FIX 5: Use getFilePath ---
        Writer writer = new FileWriter(getFilePath("db/balanceDB.txt"));
        writer.write(newInfo);
        writer.close();
    }

    void writeTransaction(int accNo, int rAccNo, int tAmount, String tRemarks) throws IOException {
        debitWrite(accNo, rAccNo, tAmount, tRemarks);
        creditWrite(accNo, rAccNo, tAmount, tRemarks);
    }

    void debitWrite(int accNo, int rAccNo, int tAmount, String tRemarks) throws IOException {
        String description = ("Transfer to " + rAccNo);
        String type = "Debit";
        String date = java.time.LocalDate.now().toString();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = formatter.format(now);
        // --- FIX 6: Use getFilePath ---
        Writer writer = new FileWriter(getFilePath("db/Bank Statement/acc_"+accNo+".txt"), true);
        writer.write(description + " " + type + " " + tAmount + " " + tRemarks + " " + date + " " + time + "\n");
        writer.close();
    }

    void creditWrite(int accNo, int rAccNo, int tAmount, String tRemarks) throws IOException {
        String description = ("Transfer from " + accNo);
        String type = "Credit";
        String date = java.time.LocalDate.now().toString();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = formatter.format(now);
        // --- FIX 7: Use getFilePath ---
        Writer writer = new FileWriter(getFilePath("db/Bank Statement/acc_"+rAccNo+".txt"),true);
        writer.write(description + " " + type + " " + tAmount + " " + tRemarks + " " + date + " " + time + "\n");
        writer.close();
    }
}