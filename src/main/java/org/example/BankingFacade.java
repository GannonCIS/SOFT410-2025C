package org.example;

import java.io.IOException;
import java.util.Scanner;

public class BankingFacade {
    private Login loginFun;
    private Creation creationAccFun;
    private BalanceInquiry balanceInquiryFun;
    private AccountDetails accountDetailsFun;
    private Transaction transactionFun;
    private BankStatement bankStatementFun;
    private Deletion deletionFun;
    private Scanner scanner;

    public BankingFacade() {
        loginFun = new Login();
        creationAccFun = new Creation();
        balanceInquiryFun = new BalanceInquiry();
        accountDetailsFun = new AccountDetails();
        transactionFun = new Transaction();
        bankStatementFun = new BankStatement();
        deletionFun = new Deletion();
        scanner = new Scanner(System.in);
    }

    public void startApp() throws IOException {
        System.out.println("┌───────────────────────────────┐");
        System.out.println("│ Welcome to SawOnGam Bank Ltd. │");
        System.out.println("├───────────────────────────────┤");
        System.out.println("│ Type 1: Login                 │");
        System.out.println("│ Type 2: Create Account        │");
        System.out.println("└───────────────────────────────┘");

        while (true) {
            if (scanner.hasNextInt()) {
                int choiceAcc = scanner.nextInt();
                if (choiceAcc == 1) {
                    handleLogin();
                    break;
                } else if (choiceAcc == 2) {
                    handleCreateAccount();
                    break;
                }
            }
            scanner.nextLine();
            System.out.println("Incorrect! Choose a valid option again.");
            System.out.println("Type 1: Login | Type 2: Create Account");
        }
    }

    private void handleLogin() throws IOException {
        loginFun.loginFun();
    }

    private void handleCreateAccount() throws IOException {
        creationAccFun.createAccFun();
    }

    public void menu(int accNo) throws IOException {
        System.out.println("┌────────────────────────────┐");
        System.out.println("│           Menu:            │");
        System.out.println("│                            │");
        System.out.println("│ Type 1: Balance Inquiry    │");
        System.out.println("│ Type 2: Account Details    │");
        System.out.println("│ Type 3: Fund Transfer      │");
        System.out.println("│ Type 4: Bank Statement     │");
        System.out.println("│ Type 5: Account Closure    │");
        System.out.println("│ Type 6: Log out            │");
        System.out.println("│ Type 7: Exit               │");
        System.out.println("└────────────────────────────┘");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> inquireBalance(accNo);
            case 2 -> viewAccountDetails(accNo);
            case 3 -> transferFunds(accNo);
            case 4 -> viewStatement(accNo);
            case 5 -> closeAccount(accNo);
            case 6 -> {
                System.out.println("Logged out successfully!");
                startApp();
            }
            case 7 -> System.exit(0);
            default -> {
                System.out.println("Incorrect! Choose a valid option again.\n");
                menu(accNo);
            }
        }
    }

    public void inquireBalance(int accNo) throws IOException {
        balanceInquiryFun.balanceInquiryFun(accNo);
        menu(accNo);
    }

    public void viewAccountDetails(int accNo) throws IOException {
        accountDetailsFun.accountDetailsFun(accNo);
        menu(accNo);
    }

    public void transferFunds(int accNo) throws IOException {
        transactionFun.transactionFun(accNo);
        menu(accNo);
    }

    public void viewStatement(int accNo) throws IOException {
        bankStatementFun.bankStatementFun(accNo);
        menu(accNo);
    }

    public void closeAccount(int accNo) throws IOException {
        deletionFun.accCloseFun(accNo, "db/credentials.txt");
        deletionFun.delLine(accNo, "db/userDB.txt");
        deletionFun.delLine(accNo, "db/balanceDB.txt");
        System.out.println("\nAccount successfully Deleted.");
        System.exit(0);
    }
}