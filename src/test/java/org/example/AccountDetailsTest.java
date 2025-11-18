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
public class AccountDetailsTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private AccountDetailsSpy accountDetailsSpy;
    private File dbDir;
    private Path userPath;

    private static final int TEST_ACC_NO = 40000;
    private static final int NON_EXISTENT_ACC = 99999;
    private static final String USER_FILE_NAME = "userDB.txt";
    private static final String NL = System.lineSeparator();

    // --- Test Data ---
    private static final String FULL_NAME = "Alice Smith";
    private static final String DOB = "1995-05-05";
    private static final String GENDER = "Female";
    private static final String ADDRESS = "123_Test_Lane";
    private static final String PHONE = "5551234567";
    private static final String EMAIL = "alice@example.com";
    private static final String ID = "ID98765";

    @Before
    public void setUp() throws IOException {
        // 1. Setup I/O redirection
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // 2. Setup mock database structure inside the temporary folder
        dbDir = tempFolder.newFolder("db");
        userPath = Path.of(dbDir.getAbsolutePath(), USER_FILE_NAME);

        // Initialize the userDB.txt with test data
        String userData = "10000 John Doe 1990-01-01 Male 1st_St 555-1234 user@mail.com ID1" + NL
                + TEST_ACC_NO + " " + FULL_NAME.replace(" ", " ") + " " + DOB + " " + GENDER + " " + ADDRESS + " " + PHONE + " " + EMAIL + " " + ID + NL
                + "50000 Bob Jones 2000-10-10 Male 3rd_Rd 555-9012 bob@mail.com ID3";
        Files.writeString(userPath, userData);

        // Mock System.in for the final scanner.nextLine() (continuation prompt)
        System.setIn(new ByteArrayInputStream(NL.getBytes()));

        // 3. Initialize the spy object
        accountDetailsSpy = new AccountDetailsSpy(dbDir.getAbsolutePath());
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
    private class AccountDetailsSpy extends AccountDetails {
        private final String dbPath;
        public int menuCallAccNo = -1;

        public AccountDetailsSpy(String tempPath) {
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
    }

    // --- Test Cases ---

    @Test
    public void testAccountDetailsFun_DetailsFoundAndDisplayed() throws IOException {
        // Act
        accountDetailsSpy.accountDetailsFun(TEST_ACC_NO);

        // Assert 1: Verify console output contains all details
        String output = outputStream.toString();

        Assert.assertTrue("Output should contain the account details title.", output.contains("Account Details:"));
        Assert.assertTrue("Full name is incorrect.", output.contains("Full Name: " + FULL_NAME));
        Assert.assertTrue("Account number is incorrect.", output.contains("Account Number: " + TEST_ACC_NO));
        Assert.assertTrue("Gender is incorrect.", output.contains("Gender: " + GENDER));
        Assert.assertTrue("Address is incorrect.", output.contains("Address: " + ADDRESS));
        Assert.assertTrue("Phone number is incorrect.", output.contains("Phone number: " + PHONE));
        Assert.assertTrue("Identification is incorrect.", output.contains("Identification: " + ID));

        // Assert 2: Verify application proceeds to the main menu
        Assert.assertEquals("Menu call interceptor should be called with the correct account number.",
                TEST_ACC_NO, accountDetailsSpy.menuCallAccNo);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testAccountDetailsFun_DetailsNotFound_ThrowsException() throws IOException {
        // Arrange: Use a non-existent account number (NON_EXISTENT_ACC)

        // Act
        // We expect the function to crash on detail[1] because wholeDetail is "" and detail is [""]
        accountDetailsSpy.accountDetailsFun(NON_EXISTENT_ACC);

        // Note: The assertion Assert.fail is not needed here because @Test(expected = ...) handles the check.
        // If the code reaches the end of the method without crashing, the test will fail due to the expected exception not being thrown.
    }
}