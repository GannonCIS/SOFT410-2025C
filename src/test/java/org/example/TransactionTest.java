package org.example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.List;

@RunWith(JUnit4.class)
public class TransactionTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private TransactionSpy transactionSpy;
    private File dbDir;
    private File statementDir;
    private Path balancePath;

    private static final int SENDER_ACC_NO = 10000;
    private static final int RECEIVER_ACC_NO = 20000;
    private static final int INVALID_ACC_NO = 99999;
    private static final int INITIAL_SENDER_BALANCE = 500;
    private static final int INITIAL_RECEIVER_BALANCE = 100;
    private static final String NL = System.lineSeparator();


    @Before
    public void setUp() throws IOException {
        // 1. Setup I/O redirection
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // 2. Setup mock database structure inside the temporary folder
        dbDir = tempFolder.newFolder("db");
        statementDir = tempFolder.newFolder("db", "Bank Statement");
        balancePath = Path.of(dbDir.getAbsolutePath(), "balanceDB.txt");

        // Initialize the balanceDB.txt with test data
        String initialBalances = SENDER_ACC_NO + " " + INITIAL_SENDER_BALANCE + NL
                + RECEIVER_ACC_NO + " " + INITIAL_RECEIVER_BALANCE;
        Files.writeString(balancePath, initialBalances);

        // 3. Initialize the spy object
        transactionSpy = new TransactionSpy(dbDir.getAbsolutePath());
    }

    @After
    public void tearDown() {
        // Restore original System streams
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Helper method to simulate user input for a transaction.
     * Only the four required pieces of input are provided. The application bug fix
     * handles the resulting stream closure gracefully.
     */
    private void provideTransactionInput(int rAccNo, int tAmount, String tRemarks) {
        // We only need the three user inputs, plus one token for the final confirmation.
        String input = rAccNo + NL
                + tAmount + NL
                + tRemarks + NL
                + "PressEnter" + NL;      // Final confirmation token
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    /**
     * Spy class to intercept hardcoded file paths and application flow control.
     */
    private class TransactionSpy extends Transaction {
        private final String dbPath;
        public int menuCallAccNo = -1;

        public TransactionSpy(String tempPath) {
            this.dbPath = tempPath;
        }

        // Intercepts the hardcoded file path generation
        @Override
        String getFilePath(String fileName) {
            // Replaces "db/" at the start of the path with the temporary folder path
            return fileName.replace("db/", dbPath + File.separator);
        }

        // Intercepts the Main.menu() call on success
        @Override
        void menuCall(int accNo) throws IOException {
            this.menuCallAccNo = accNo;
        }
    }

    // --- Helper to read account balance ---
    private int getBalanceFromFile(int accNo) throws IOException {
        return Files.lines(balancePath)
                .filter(line -> line.startsWith(String.valueOf(accNo)))
                .map(line -> Integer.parseInt(line.split(" ")[1]))
                .findFirst()
                .orElseThrow(() -> new IOException("Account not found in file"));
    }

    // --- Test Cases for Successful Transaction ---

    @Test
    public void testTransactionFun_Success() throws IOException {
        // Arrange
        final int TRANSFER_AMOUNT = 150;
        final String REMARK = "Rent Payment";

        provideTransactionInput(RECEIVER_ACC_NO, TRANSFER_AMOUNT, REMARK);

        // Act
        transactionSpy.transactionFun(SENDER_ACC_NO);

        // Assert 1: Verify balances are updated correctly in the file
        Assert.assertEquals("Sender balance should be debited.",
                INITIAL_SENDER_BALANCE - TRANSFER_AMOUNT, getBalanceFromFile(SENDER_ACC_NO));
        Assert.assertEquals("Receiver balance should be credited.",
                INITIAL_RECEIVER_BALANCE + TRANSFER_AMOUNT, getBalanceFromFile(RECEIVER_ACC_NO));

        // Assert 2: Verify transaction statement files were written
        Path senderStatementPath = statementDir.toPath().resolve("acc_" + SENDER_ACC_NO + ".txt");
        Path receiverStatementPath = statementDir.toPath().resolve("acc_" + RECEIVER_ACC_NO + ".txt");

        String senderStatement = Files.readAllLines(senderStatementPath).stream().collect(Collectors.joining(" "));
        String receiverStatement = Files.readAllLines(receiverStatementPath).stream().collect(Collectors.joining(" "));

        Assert.assertTrue("Sender statement must contain debit entry.",
                senderStatement.contains("Transfer to " + RECEIVER_ACC_NO + " Debit " + TRANSFER_AMOUNT + " " + REMARK));
        Assert.assertTrue("Receiver statement must contain credit entry.",
                receiverStatement.contains("Transfer from " + SENDER_ACC_NO + " Credit " + TRANSFER_AMOUNT + " " + REMARK));

        // Assert 3: Verify success message and menu call
        Assert.assertTrue("Output should contain 'Transaction Successful!'.",
                outputStream.toString().contains("Transaction Successful!"));
        Assert.assertEquals("Menu call interceptor should be called.",
                SENDER_ACC_NO, transactionSpy.menuCallAccNo);
    }

    // --- Test Cases for Failure Scenarios ---

    @Test
    public void testTransactionFun_InsufficientBalance() throws IOException {
        // Arrange
        final int OVER_AMOUNT = 600; // Greater than INITIAL_SENDER_BALANCE (500)

        provideTransactionInput(RECEIVER_ACC_NO, OVER_AMOUNT, "Too Much");

        // Act
        transactionSpy.transactionFun(SENDER_ACC_NO);

        // Assert 1: Verify balances are UNCHANGED
        Assert.assertEquals("Sender balance should be unchanged.",
                INITIAL_SENDER_BALANCE, getBalanceFromFile(SENDER_ACC_NO));
        Assert.assertEquals("Receiver balance should be unchanged.",
                INITIAL_RECEIVER_BALANCE, getBalanceFromFile(RECEIVER_ACC_NO));

        // Assert 2: Verify failure message
        Assert.assertTrue("Output should contain 'Insufficient Balance!'.",
                outputStream.toString().contains("Insufficient Balance!"));

        // Assert 3: Menu should NOT be called on failure
        Assert.assertEquals(-1, transactionSpy.menuCallAccNo);
    }

    @Test
    public void testTransactionFun_IncorrectReceiverAccNo() throws IOException {
        // Arrange
        final int TRANSFER_AMOUNT = 50;

        provideTransactionInput(INVALID_ACC_NO, TRANSFER_AMOUNT, "Wrong Acc");

        // Act
        transactionSpy.transactionFun(SENDER_ACC_NO);

        // Assert 1: Verify balances are UNCHANGED
        Assert.assertEquals("Sender balance should be unchanged.",
                INITIAL_SENDER_BALANCE, getBalanceFromFile(SENDER_ACC_NO));

        // Assert 2: Verify failure message
        Assert.assertTrue("Output should contain 'Incorrect Account Number!'.",
                outputStream.toString().contains("Incorrect Account Number!"));

        // Assert 3: Menu should NOT be called on failure
        Assert.assertEquals(-1, transactionSpy.menuCallAccNo);
    }
}