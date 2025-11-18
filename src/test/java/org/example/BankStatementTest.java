package org.example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;


@RunWith(JUnit4.class)
public class BankStatementTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private BankStatementSpy bankStatementSpy;
    private File dbDir;
    private File statementDir;
    private File testStatementFile;

    private static final int TEST_ACC_NO = 123456;
    private static final String NL = System.lineSeparator();
    private static final String FILE_NAME = "acc_" + TEST_ACC_NO + ".txt";


    @Before
    public void setUp() throws IOException {
        // 1. Setup I/O redirection
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // 2. Setup mock database structure inside the temporary folder
        dbDir = tempFolder.newFolder("db");
        statementDir = tempFolder.newFolder("db", "Bank Statement");
        testStatementFile = new File(statementDir, FILE_NAME);

        // Mock System.in for the exit method's scanner.nextLine()
        // We simulate pressing the Enter key to continue.
        System.setIn(new ByteArrayInputStream(NL.getBytes()));

        // 3. Initialize the spy object
        bankStatementSpy = new BankStatementSpy(dbDir.getAbsolutePath());
    }

    @After
    public void tearDown() {
        // Restore original System streams
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Spy class to intercept hardcoded file paths and application flow control.
     */
    private class BankStatementSpy extends BankStatement {
        private final String dbPath;
        public int menuCallAccNo = -1;

        public BankStatementSpy(String tempPath) {
            // Adjust the path to end without the trailing File.separator, 
            // as the original getFilePath replacement logic requires it.
            this.dbPath = tempPath;
        }

        // Intercepts the hardcoded file path generation
        @Override
        String getFilePath(String fileName) {
            // The original file uses "db/...", so we replace that prefix with the temp path.
            return fileName.replace("db/", dbPath + File.separator);
        }

        // Intercepts the Main.menu() call on success
        @Override
        void menuCall(int accNo) throws IOException {
            this.menuCallAccNo = accNo;
        }
    }

    // --- Test Cases ---

    @Test
    public void testBankStatementFun_FileExists_OneTransaction() throws IOException {
        // Arrange: Create a temporary file with one transaction line
        String transactionLine = "Deposited from 987654 Debit 5000 Salary 2024-05-10 10:30:00";
        try (FileWriter writer = new FileWriter(testStatementFile)) {
            writer.write(transactionLine + NL);
        }

        // Act
        bankStatementSpy.bankStatementFun(TEST_ACC_NO);

        // Assert 1: Check console output
        String actualOutput = outputStream.toString();

        Assert.assertTrue("Output should contain the transaction data.",
                actualOutput.contains("Deposited from 987654"));
        Assert.assertTrue("Output should contain the amount.",
                actualOutput.contains("$5000"));

        // Assert 2: Verify flow control
        Assert.assertTrue("Output should prompt the user to continue.",
                actualOutput.contains("Press Enter key to continue..."));
        Assert.assertEquals("Menu call interceptor should be called.",
                TEST_ACC_NO, bankStatementSpy.menuCallAccNo);
    }

    @Test
    public void testBankStatementFun_FileExists_MultipleTransactions() throws IOException {
        // Arrange: Create a temporary file with multiple transaction lines
        String transaction1 = "Transfer to 999999 Debit 100 Shopping 2024-05-11 12:00:00";
        String transaction2 = "Transfer from 111111 Credit 250 Gift 2024-05-11 15:30:00";
        try (FileWriter writer = new FileWriter(testStatementFile)) {
            writer.write(transaction1 + NL);
            writer.write(transaction2 + NL);
        }

        // Act
        bankStatementSpy.bankStatementFun(TEST_ACC_NO);

        // Assert 1: Check console output for both transactions
        String actualOutput = outputStream.toString();

        Assert.assertTrue("Output must contain Transaction 1 description.",
                actualOutput.contains("Transfer to 999999"));
        Assert.assertTrue("Output must contain Transaction 2 amount.",
                actualOutput.contains("$250"));

        // Assert 2: Verify flow control
        Assert.assertEquals("Menu call interceptor should be called.",
                TEST_ACC_NO, bankStatementSpy.menuCallAccNo);
    }

    @Test
    public void testBankStatementFun_FileNotFound() throws IOException {
        // Arrange: Ensure the file is deleted so the FileNotFoundException is triggered
        Files.deleteIfExists(testStatementFile.toPath());

        // Act
        bankStatementSpy.bankStatementFun(TEST_ACC_NO);

        // Assert 1: Check console output for the error message
        String actualOutput = outputStream.toString();

        Assert.assertTrue("Output should show the 'No Transaction found!' message.",
                actualOutput.contains("No Transaction found!"));

        // Assert 2: Verify flow control immediately moves to exit/menu
        Assert.assertTrue("Output should prompt the user to continue.",
                actualOutput.contains("Press Enter key to continue..."));
        Assert.assertEquals("Menu call interceptor should be called.",
                TEST_ACC_NO, bankStatementSpy.menuCallAccNo);
    }
}