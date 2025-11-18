package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class BalanceInquiry {

    String getFilePath(String fileName) {
        return fileName;
    }

    void balanceInquiryFun(int accNo) throws IOException {
        File file = new File(getFilePath("db/balanceDB.txt"));
        Scanner scanner = new Scanner(file);
        int accBalance = -1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] subLine = line.split(" ");
            if (accNo == Integer.parseInt(subLine[0])) {
                accBalance = Integer.parseInt(subLine[1]);
                break;
            }
        }
        if (accBalance == -1) {
            System.out.println("We're having some issues, Try Again!");
            systemExit(0); // <--- Intercepted call
        } else {
            System.out.println("┌───────────────────────────────┐");
            System.out.println("  Your current balance is $"+ accBalance +"   ");
            System.out.println("└───────────────────────────────┘");
            System.out.println("Press Enter key to continue...");
            Scanner scanner1 = new Scanner(System.in);
            scanner1.nextLine();
            menuCall(accNo);
        }
    }

    void systemExit(int status) {
        System.exit(status);
    }

    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }
}