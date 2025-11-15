package org.example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Login {

    File file = new File("db/credentials.txt");
    private int accNo;
    private String pass;
    Scanner scanner = new Scanner(System.in);

    void loginFun() throws IOException {
        System.out.print("Enter your Account Number: ");
        this.accNo = scanner.nextInt();
        System.out.print("Enter your Password: ");
        this.pass = scanner.next();
        loginAuth();
    }

     void loginAuth() throws IOException {
        Scanner scanFile = new Scanner(file);
        Scanner scanPass = new Scanner(file);
        boolean loginBoo = findLogin(scanFile);
        boolean incPass = findPassword(scanPass);
        scanFile.close();
        if (loginBoo) {
            System.out.println("Login Successful!!\n");
            Main.menu(accNo);
        } else if (incPass) {
            System.out.println("\nIncorrect Password!");
            System.out.println("Please enter again.\n");
            loginFun();
        } else {
            System.out.println("\nAccount doesn't exists!");
            System.out.println("Please enter again.\n");
            loginFun();
        }
    }

    private boolean findLogin(Scanner scanFile) throws IOException {
        boolean loginBoo = false;
        while (scanFile.hasNextLine()) {
            String line = scanFile.nextLine();
            String[] subLine = line.split(" ");
            if (accNo == Integer.parseInt(subLine[0]) && pass.equals(subLine[1])) {
                loginBoo = true;
                break;
            }
        }
        return loginBoo;
    }

    private boolean findPassword(Scanner scanFile) {
        boolean incPass = false;
        while (scanFile.hasNextLine()) {
            String line = scanFile.nextLine();
            String[] subLine = line.split(" ");
            if (accNo == Integer.parseInt(subLine[0])) {
                incPass = true;
            }
        }
        return incPass;
    }

    // Test-only method to allow setting the file path without modifying the original structure
    public void setFile(File file) {
        this.file = file;
    }

    // Test-only method to allow setting input fields for direct authentication testing
    public void setCredentials(int accNo, String pass) {
        this.accNo = accNo;
        this.pass = pass;
    }
}


