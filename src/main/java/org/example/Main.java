package org.example;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {


    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        BankingFacade facade = new BankingFacade();
        facade.startApp();
    }

    public static void menu(int accNo) throws IOException {
        new BankingFacade().menu(accNo);
    }
}