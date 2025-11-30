package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionDataRepository {

    public interface PathResolver {
        String getFilePath(String fileName);
    }

    private final PathResolver pathResolver;

    public TransactionDataRepository(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void recordDebit(TransactionDetails details) throws IOException {
        String description = ("Transfer to " + details.getReceiverAccNo());
        String type = "Debit";

        String date = java.time.LocalDate.now().toString();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = formatter.format(now);

        String filePath = pathResolver.getFilePath("db/Bank Statement/acc_" + details.getSenderAccNo() + ".txt");

        try (Writer writer = new FileWriter(filePath, true)) {
            String recordLine = String.format("%s %s %d %s %s %s%n",
                    description, type, details.getAmount(), details.getRemarks(), date, time);
            writer.write(recordLine);
        }
    }

    public void recordCredit(TransactionDetails details) throws IOException {
        String description = ("Transfer from " + details.getSenderAccNo());
        String type = "Credit";

        String date = java.time.LocalDate.now().toString();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = formatter.format(now);

        String filePath = pathResolver.getFilePath("db/Bank Statement/acc_" + details.getReceiverAccNo() + ".txt");

        try (Writer writer = new FileWriter(filePath, true)) {
            String recordLine = String.format("%s %s %d %s %s %s%n",
                    description, type, details.getAmount(), details.getRemarks(), date, time);
            writer.write(recordLine);
        }
    }
}