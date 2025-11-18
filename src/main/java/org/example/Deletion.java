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

        // --- FIX 1: Use a loop and read full lines for robust input ---
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
        String newInfo = "";
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // Prevent empty lines from being processed
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] subLine = line.split(" ");
            int countLine = subLine.length;
            if (accNo == Integer.parseInt(subLine[0])) {
                continue;
            }
            String newLine = "";
            for (int x=0;x < countLine; x++){
                newLine += subLine[x] + " ";
            }

            // --- FIX 2: Use System.lineSeparator() for consistent newlines ---
            newInfo += newLine.trim() + System.lineSeparator();
        }

        // Check if the last line is blank
        if (newInfo.endsWith(System.lineSeparator())) {
            // Remove the trailing newline
            newInfo = newInfo.substring(0, newInfo.length() - System.lineSeparator().length());
        }

        FileWriter writer = new FileWriter(fileName);
        writer.write(newInfo);
        writer.close();
        scanner.close();

    }

}