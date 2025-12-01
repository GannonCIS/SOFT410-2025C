package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Login {

    File file = new File("db/credentials.txt");
    protected int accNo;
    protected String pass;
    Scanner scanner = new Scanner(System.in);

    void loginFun() throws IOException {
        System.out.print("Enter your Account Number: ");
        this.accNo = scanner.nextInt();
        System.out.print("Enter your Password: ");
        this.pass = scanner.next();
        loginAuth();
    }

    void loginAuth() throws IOException {
        boolean loginBoo = findLogin(new Scanner(file));
        boolean incPass = findPassword(new Scanner(file));

        if (loginBoo) {
            System.out.println("Login Successful!!\n");
            menuCall(accNo);
        } else if (incPass) {
            System.out.println("\nIncorrect Password!");
            System.out.println("Please enter again.\n");
            loginFun(); // Recursive call
        } else {
            System.out.println("\nAccount doesn't exists!");
            System.out.println("Please enter again.\n");
            loginFun(); // Recursive call
        }
    }

    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }

    private boolean findLogin(Scanner scanner) throws IOException {
        boolean loginBoo = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            String[] subLine = line.split(" ");
            if (accNo == Integer.parseInt(subLine[0]) && pass.equals(subLine[1])) {
                loginBoo = true;
                break;
            }
        }
        scanner.close(); // Close the scanner
        return loginBoo;
    }

    private boolean findPassword(Scanner scanner) {
        boolean incPass = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            String[] subLine = line.split(" ");
            if (accNo == Integer.parseInt(subLine[0])) {
                incPass = true;
                break; // Found the account, no need to keep scanning
            }
        }
        scanner.close(); // Close the scanner
        return incPass;
    }

}