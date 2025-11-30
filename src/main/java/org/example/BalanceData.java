package org.example;

import java.io.*;
import java.util.Scanner;

public class BalanceData {

    private final String BALANCE_FILE_PATH;

    public BalanceData(String basePath) {
        // Use the injected base path to determine the actual balance file path
        // This ensures that when the test calls it, it uses the temp folder path.
        this.BALANCE_FILE_PATH = basePath + "balanceDB.txt";
    }

    public int getBalance(int accNo) throws FileNotFoundException {
        File file = new File(BALANCE_FILE_PATH);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;

            String[] subLine = line.split(" ");
            int account = Integer.parseInt(subLine[0]);

            if (accNo == account) {
                int balance = Integer.parseInt(subLine[1]);
                scanner.close();
                return balance;
            }
        }
        scanner.close();
        return -1;
    }

    public void updateBalances(int senderAccNo, int receiverAccNo, int tAmount) throws IOException {
        File file = new File(BALANCE_FILE_PATH);
        Scanner scanner = new Scanner(file);
        StringBuilder newInfo = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;

            String[] subLine = line.split(" ");
            int currentAccNo = Integer.parseInt(subLine[0]);
            int currentBalance = Integer.parseInt(subLine[1]);

            if (senderAccNo == currentAccNo) {
                currentBalance = currentBalance - tAmount;
            } else if (receiverAccNo == currentAccNo) {
                currentBalance = currentBalance + tAmount;
            }

            newInfo.append(currentAccNo).append(" ").append(currentBalance).append("\n");
        }
        scanner.close();

        try (FileWriter writer = new FileWriter(BALANCE_FILE_PATH)) {
            writer.write(newInfo.toString());
        }
    }

    public boolean accountExists(int accNo) throws FileNotFoundException {
        File file = new File(BALANCE_FILE_PATH);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            String[] subLine = line.split(" ");

            if (accNo == Integer.parseInt(subLine[0])) {
                scanner.close();
                return true;
            }
        }
        scanner.close();
        return false;
    }
}