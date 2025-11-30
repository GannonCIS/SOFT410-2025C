package org.example;

import java.io.FileNotFoundException;

public class TransactionValidator {

    private final BalanceData balanceData;

    // Dependency on BalanceData is needed to perform existence and balance checks
    public TransactionValidator(BalanceData balanceData) {
        this.balanceData = balanceData;
    }

    //Checks if the receiver account number exists in the database.
    public boolean isReceiverAccountValid(int rAccNo) throws FileNotFoundException {
        // Renamed from rAccCheck for clarity
        return balanceData.accountExists(rAccNo);
    }


    //Checks if the sender account has a sufficient balance for the transfer amount.
    public boolean isSufficientBalance(int accNo, int tAmount) throws FileNotFoundException {
        // Renamed from sAccBalCheck for clarity
        int currentBalance = balanceData.getBalance(accNo);
        // Returns true if account was found (balance != -1) AND balance is sufficient
        return currentBalance != -1 && tAmount <= currentBalance;
    }
}