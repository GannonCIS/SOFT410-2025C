package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class AccountRepository {

    public interface PathResolver {
        String getFilePath(String fileName);
    }

    private static final int INITIAL_BALANCE = 100;
    private final PathResolver pathResolver;

    public AccountRepository(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void credWrite(Account account) throws IOException {
        String filePath = pathResolver.getFilePath("db/credentials.txt");
        try (Writer writer = new FileWriter(filePath, true)) {
            writer.write(System.lineSeparator() + account.getAccountNumber() + " " + account.getPassword());
        }
    }

    public void balWrite(int accNo) throws IOException {
        String filePath = pathResolver.getFilePath("db/balanceDB.txt");
        try (Writer writer = new FileWriter(filePath, true)) {
            writer.write(System.lineSeparator() + accNo + " " + INITIAL_BALANCE);
        }
    }

    public void userWrite(Account account) throws IOException {
        String filePath = pathResolver.getFilePath("db/userDB.txt");
        try (Writer writer = new FileWriter(filePath, true)) {
            writer.write(System.lineSeparator() + account.toUserDBLine());
        }
    }
}