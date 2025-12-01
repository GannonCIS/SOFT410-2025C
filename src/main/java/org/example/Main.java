package org.example;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        BankingFacade facade = new BankingFacade();
        facade.startApp();
    }

    public static void menu(int accNo) throws IOException {
        new BankingFacade().menu(accNo);
    }
}