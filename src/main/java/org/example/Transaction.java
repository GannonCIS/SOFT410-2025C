package org.example;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Transaction implements TransactionDataRepository.PathResolver{

    private final BalanceData balanceData;
    private final BankUserInput inputHandler;
    private final TransactionDataRepository recordRepository;
    private final TransactionValidator validator;

    public Transaction() {
        String dbPath = getFilePath("db" + File.separator);
        this.balanceData = new BalanceData(dbPath);
        this.inputHandler = new BankUserInput();
        this.recordRepository = new TransactionDataRepository(this);
        this.validator = new TransactionValidator(this.balanceData);
    }

    // Constructor for testing
    protected Transaction(BalanceData balanceData) {
        this.balanceData = balanceData;
        this.inputHandler = new BankUserInput();
        this.recordRepository = new TransactionDataRepository(this);
        this.validator = new TransactionValidator(this.balanceData);
    }

    protected Transaction(BalanceData balanceData, BankUserInput inputHandler, TransactionDataRepository recordRepository, TransactionValidator validator) {
        this.balanceData = balanceData;
        this.inputHandler = inputHandler;
        this.recordRepository = recordRepository;
        this.validator = validator;
    }

    // Updated Fallback protected constructor for testing purposes
    protected Transaction(BalanceData balanceData, BankUserInput inputHandler, TransactionDataRepository recordRepository) {
        this.balanceData = balanceData;
        this.inputHandler = inputHandler;
        this.recordRepository = recordRepository;
        this.validator = new TransactionValidator(this.balanceData);
    }

    // Fallback protected constructor for testing purposes (original signature)
    protected Transaction(BalanceData balanceData, BankUserInput inputHandler) {
        this(balanceData, inputHandler, new TransactionDataRepository(new Transaction()));
    }

    @Override
    public String getFilePath(String fileName) {
        return fileName;
    }

    void transactionFun(int accNo) throws IOException {
        TransactionDetails details = inputHandler.getTransferDetails(accNo);
        allTransaction(details);
    }

    void allTransaction(TransactionDetails details) throws IOException {
        int accNo = details.getSenderAccNo();
        int rAccNo = details.getReceiverAccNo();
        int tAmount = details.getAmount();
        String tRemarks = details.getRemarks();

        if (validator.isReceiverAccountValid(rAccNo)) {
            if (validator.isSufficientBalance(accNo, tAmount)) {
                transaction(accNo, rAccNo, tAmount);  //actual transaction
                writeTransaction(details); //write transaction to file
                System.out.println("Transaction Successful!");
                System.out.println("Press any key to continue...");

                try (Scanner tscanner = new Scanner(System.in)) {
                    if (tscanner.hasNextLine()) {
                        tscanner.nextLine();
                    }
                }
                menuCall(accNo);
            } else {
                System.out.println("Insufficient Balance!");
            }
        } else {
            System.out.println("Incorrect Account Number!");
        }
    }

    void transaction(int accNo, int rAccNo, int tAmount) throws IOException {
        balanceData.updateBalances(accNo, rAccNo, tAmount);
    }

    void writeTransaction(TransactionDetails details) throws IOException {
        recordRepository.recordDebit(details);
        recordRepository.recordCredit(details);
    }

    //Needed for testing
    void menuCall(int accNo) throws IOException {
        Main.menu(accNo);
    }
}