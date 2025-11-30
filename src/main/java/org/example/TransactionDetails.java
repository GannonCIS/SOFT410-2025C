package org.example;

public class TransactionDetails {
    private final int senderAccNo;
    private final int receiverAccNo;
    private final int amount;
    private final String remarks;

    public TransactionDetails(int senderAccNo, int receiverAccNo, int amount, String remarks) {
        this.senderAccNo = senderAccNo;
        this.receiverAccNo = receiverAccNo;
        this.amount = amount;
        this.remarks = remarks;
    }

    public int getSenderAccNo() {
        return senderAccNo;
    }

    public int getReceiverAccNo() {
        return receiverAccNo;
    }

    public int getAmount() {
        return amount;
    }

    public String getRemarks() {
        return remarks;
    }
}