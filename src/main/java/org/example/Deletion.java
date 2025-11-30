package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Deletion {

    void accCloseFun(int accNo, String fileName) throws IOException {
        System.out.println("Are you sure want to delete your account?");
        System.out.println("Type 1: Yes");
        System.out.println("Type 2: No");
        Scanner tscanner = new Scanner(System.in);

        while (true) {
            int conf = -1;
            try {
                // Read the whole line, then parse
                String line = tscanner.nextLine();
                conf = Integer.parseInt(line);
            } catch (Exception e) {
                // Handle cases where input is not a number or stream ends
                conf = -1; // Set to invalid choice
            }

            if (conf == 2) {
                // User chose "No"
                menuCall(accNo);
                return; // Exit the function without deleting

            } else if (conf == 1) {
                // User chose "Yes"
                break; // Exit the loop and proceed to delLine

            } else {
                // Invalid input
                System.out.println("Incorrect! Choose a valid option again.\n");
                // Loop continues to ask for input
            }
        }

        // delLine is now only reachable if conf == 1
        delLine(accNo, fileName);
    }

    // Interceptor for Main.menu()
    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }

    void delLine(int accNo, String fileName) throws IOException {
        File file = new File(fileName);
        String newInfo = readContent(accNo, file);
        writeContent(fileName, newInfo);
    }

    private String readContent(int accNo, File file) throws IOException {
        StringBuilder newInfo = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String processedLine = processLine(line, accNo);
                if (processedLine != null) {
                    newInfo.append(processedLine).append(System.lineSeparator());
                }
            }
        }
        if (newInfo.length() > 0 && newInfo.toString().endsWith(System.lineSeparator())) {
            newInfo.setLength(newInfo.length() - System.lineSeparator().length());
        }
        return newInfo.toString();
    }

    private String processLine(String line, int accNo) {
        if (line.trim().isEmpty()) {
            return null;
        }
        String[] subLine = line.split(" ");
        try {
            if (accNo == Integer.parseInt(subLine[0])) {
                return null;
            }
        } catch (NumberFormatException e) {
            System.err.println("Skipping corrupted line in DB: " + line);
            return null;
        }
        String newLine = String.join(" ", subLine).trim();
        return newLine;
    }

    private void writeContent(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }
    }
}