package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Creation implements AccountRepository.PathResolver{

    private final BankUserInput inputHandler;
    private final AccountRepository accountRepository;

    public Creation() {
        this.inputHandler = new BankUserInput();
        this.accountRepository = new AccountRepository(this);
    }

    protected Creation(BankUserInput inputHandler) {
        this.inputHandler = inputHandler;
        this.accountRepository = new AccountRepository(this);
    }

    protected Creation(BankUserInput inputHandler, AccountRepository accountRepository) {
        this.inputHandler = inputHandler;
        this.accountRepository = accountRepository;
    }

    @Override
    public String getFilePath(String fileName) {
        return fileName;
    }

    void createAccFun() throws IOException {
        int accNo = accNoCreation();
        String[] userInfo = inputHandler.getNewAccountInfo();

        Account newAccount = createAccount(accNo, userInfo);

        accountRepository.credWrite(newAccount);
        accountRepository.balWrite(newAccount.getAccountNumber());
        accountRepository.userWrite(newAccount);

        System.out.println("\nAccount created successfully!\n");
        System.out.println("Your account number is: " + newAccount.getAccountNumber());
        System.out.println("Your password is: " + newAccount.getPassword()+ "\n");

        menuCall(newAccount.getAccountNumber());
    }

    // Interceptor for Main.menu(), necessary for tests
    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }

    private Account createAccount(int accNo, String[] userInfo) {
        return new Account(
                accNo,
                userInfo[0], // First Name
                userInfo[1], // Last Name
                userInfo[2], // DOB
                userInfo[3], // Gender
                userInfo[4], // Address
                userInfo[5], // Phone Number
                userInfo[6], // Email
                userInfo[7], // Citizenship Number
                userInfo[8]  // Password
        );
    }

    int accNoCreation() throws IOException {
        String lastLine = "";
        int accNo;
        File file = new File("db/credentials.txt");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            lastLine = scanner.nextLine();
        }
        if (Objects.equals(lastLine, "")) {
            accNo = 1;
        } else {
            String[] subLine = lastLine.split(" ");
            accNo = Integer.parseInt(subLine[0]);
            accNo++;
        }
        return accNo;
    }
}