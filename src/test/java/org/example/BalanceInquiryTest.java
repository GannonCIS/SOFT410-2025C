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
public class BalanceInquiryTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private BalanceInquirySpy balanceInquirySpy;
    private File dbDir;
    private Path balancePath;

    private static final int TEST_ACC_NO = 15000;
    private static final int TEST_BALANCE = 12345;
    private static final int NON_EXISTENT_ACC = 99999;
    private static final String BALANCE_FILE_NAME = "balanceDB.txt";
    private static final String NL = System.lineSeparator();

    @Before
    public void setUp() throws IOException {
        // 1. Setup I/O redirection
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // 2. Setup mock database structure inside the temporary folder
        dbDir = tempFolder.newFolder("db");
        balancePath = Path.of(dbDir.getAbsolutePath(), BALANCE_FILE_NAME);

        // Initialize the balanceDB.txt with test data
        String balanceData = "10000 500" + NL
                + TEST_ACC_NO + " " + TEST_BALANCE + NL
                + "20000 1000";
        Files.writeString(balancePath, balanceData);

        // Mock System.in for the continuation prompt
        System.setIn(new ByteArrayInputStream(NL.getBytes()));

        // 3. Initialize the spy object
        balanceInquirySpy = new BalanceInquirySpy(dbDir.getAbsolutePath());
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
    private class BalanceInquirySpy extends BalanceInquiry {
        private final String dbPath;
        public int menuCallAccNo = -1;
        public int exitStatus = -1; // To track if System.exit was called

        public BalanceInquirySpy(String tempPath) {
            this.dbPath = tempPath;
        }

        // Intercepts the hardcoded file path generation
        @Override
        String getFilePath(String fileName) {
            // Replaces "db/..." with the temporary folder path
            return fileName.replace("db/", dbPath + File.separator);
        }

        // Intercepts the Main.menu() call on success
        @Override
        void menuCall(int accNo) throws IOException {
            this.menuCallAccNo = accNo;
        }

        // Intercepts the System.exit(0) call on failure
        @Override
        void systemExit(int status) {
            this.exitStatus = status;
            // Throw a custom runtime exception to stop execution flow
            throw new RuntimeException("SystemExitPrevented");
        }
    }

    // --- Test Cases ---

    @Test
    public void testBalanceInquiryFun_BalanceFoundAndDisplayed() throws IOException {
        // Act
        balanceInquirySpy.balanceInquiryFun(TEST_ACC_NO);

        // Assert 1: Verify console output contains the balance
        String output = outputStream.toString();
        Assert.assertTrue("Output should contain the correct formatted balance.",
                output.contains("Your current balance is $" + TEST_BALANCE));

        // Assert 2: Verify application proceeds to the main menu
        Assert.assertEquals("Menu call interceptor should be called with the correct account number.",
                TEST_ACC_NO, balanceInquirySpy.menuCallAccNo);
        Assert.assertEquals("System.exit should NOT be called.", -1, balanceInquirySpy.exitStatus);
    }

    @Test
    public void testBalanceInquiryFun_AccountNotFound() throws IOException {
        // Act & Assert: Expect the System.exit interceptor to throw its exception
        try {
            balanceInquirySpy.balanceInquiryFun(NON_EXISTENT_ACC);
            // If the code reaches here, the test should fail because System.exit(0) wasn't triggered
            Assert.fail("Expected RuntimeException (SystemExitPrevented) was not thrown.");
        } catch (RuntimeException e) {
            Assert.assertEquals("The exception message must confirm the exit was intercepted.",
                    "SystemExitPrevented", e.getMessage());
        }

        // Assert 1: Verify failure message was printed
        String output = outputStream.toString();
        Assert.assertTrue("Output should display the error message.",
                output.contains("We're having some issues, Try Again!"));

        // Assert 2: Verify System.exit was called
        Assert.assertEquals("SystemExit interceptor must have been called with status 0.",
                0, balanceInquirySpy.exitStatus);

        // Assert 3: Menu should NOT be called on failure
        Assert.assertEquals("Menu method should NOT be called on failure.",
                -1, balanceInquirySpy.menuCallAccNo);
    }
}