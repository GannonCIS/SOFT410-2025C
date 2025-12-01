package org.example;

import java.io.FileNotFoundException;

public class TransactionValidator {

    private final BalanceData balanceData;

    public TransactionValidator(BalanceData balanceData) {
        this.balanceData = balanceData;
    }

    //Checks if the receiver account number exists in the database.
    public boolean isReceiverAccountValid(int rAccNo) throws FileNotFoundException {
        return balanceData.accountExists(rAccNo);
    }


    //Checks if the sender account has a sufficient balance for the transfer amount.
    public boolean isSufficientBalance(int accNo, int tAmount) throws FileNotFoundException {
        int currentBalance = balanceData.getBalance(accNo);
        return currentBalance != -1 && tAmount <= currentBalance;
    }
}