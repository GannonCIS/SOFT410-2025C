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
import java.util.stream.Collectors;
import java.util.List;

@RunWith(JUnit4.class)
public class DeletionTest {

    // Rule for creating and automatically cleaning up temporary files/folders
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private DeletionSpy deletionSpy;

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private File dbDir;
    private File mockCredFile;
    private File mockUserFile;
    private File mockBalanceFile;

    // String containing the expected content of the credentials file before deletion
    private String initialCredsExpected;
    private final String NL = System.lineSeparator(); // Use system-specific newline

    private static final int TEST_ACC_NO = 12345;
    private static final int OTHER_ACC_NO = 54321;
    private static final String CRED_FILE_NAME = "credentials.txt";
    private static final String USER_FILE_NAME = "userDB.txt";
    private static final String BALANCE_FILE_NAME = "balanceDB.txt";

    @Before
    public void setUp() throws IOException {
        // 1. Setup I/O redirection
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // 2. Setup mock database structure inside the temporary folder
        dbDir = tempFolder.newFolder("db");

        mockCredFile = new File(dbDir, CRED_FILE_NAME);
        mockUserFile = new File(dbDir, USER_FILE_NAME);
        mockBalanceFile = new File(dbDir, BALANCE_FILE_NAME);

        // --- FIX: Use system newline for consistent comparison ---
        String initialCreds = "10000 password1" + NL + TEST_ACC_NO + " testpass" + NL + OTHER_ACC_NO + " otherpass";
        String initialUsers = "10000 John Doe 1990-01-01 Male 1st St 555-1234 user@mail.com ID1" + NL
                + TEST_ACC_NO + " Alice Smith 1995-05-05 Female 2nd Ave 555-5678 alice@mail.com ID2" + NL
                + OTHER_ACC_NO + " Bob Jones 2000-10-10 Male 3rd Rd 555-9012 bob@mail.com ID3";
        String initialBalances = "10000 500" + NL + TEST_ACC_NO + " 1500" + NL + OTHER_ACC_NO + " 2500";

        // Store the clean, expected content for later comparison (ConfirmationNo test)
        initialCredsExpected = initialCreds;

        Files.writeString(mockCredFile.toPath(), initialCreds);
        Files.writeString(mockUserFile.toPath(), initialUsers);
        Files.writeString(mockBalanceFile.toPath(), initialBalances);

        // 3. Initialize the spy object with the path to the temp directory
        deletionSpy = new DeletionSpy(dbDir.getAbsolutePath());
    }

    @After
    public void tearDown() {
        // Restore original System streams
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Helper method to simulate user input.
     */
    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    /**
     * Spy class to intercept hardcoded file paths and application flow control.
     */
    private class DeletionSpy extends Deletion {
        private final String dbPath;
        public int menuCallAccNo = -1;
        public int exitStatus = -1;

        public DeletionSpy(String tempPath) {
            this.dbPath = tempPath;
        }

        @Override
        void delLine(int accNo, String fileName) throws IOException {
            // accCloseFun calls delLine with just "db/credentials.txt",
            // so we must prepend the temp folder path.
            super.delLine(accNo, dbPath + "/" + fileName);
        }

        // Intercept the Main.menu() call (used when user chooses 'No')
        @Override
        void menuCall(int accNo) throws IOException {
            this.menuCallAccNo = accNo;
        }
    }

    // --- Test Cases for delLine (The core deletion logic) ---

    @Test
    public void testDelLine_TargetInMiddle() throws IOException {
        // Act
        deletionSpy.delLine(TEST_ACC_NO, CRED_FILE_NAME);

        // Assert
        List<String> remainingLines = Files.readAllLines(mockCredFile.toPath()).stream()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());

        String finalContent = String.join(NL, remainingLines);

        Assert.assertFalse("Credentials should not contain the deleted account.",
                finalContent.contains(String.valueOf(TEST_ACC_NO)));
        Assert.assertTrue("Credentials should contain the first account.",
                finalContent.contains(String.valueOf(10000)));

        Assert.assertEquals("The file should contain exactly 2 lines (entries) after deletion.",
                2, remainingLines.size());
    }

    // --- Test Cases for accCloseFun (Confirmation prompt and redirection) ---

    @Test
    public void testAccCloseFun_ConfirmationYes() throws IOException {
        // Arrange: Simulate "1" (Yes) confirmation input
        provideInput("1" + NL);

        // Act
        deletionSpy.accCloseFun(TEST_ACC_NO, CRED_FILE_NAME);

        // Assert 1: Check the file content (Deletion should happen)
        List<String> remainingLines = Files.readAllLines(mockCredFile.toPath()).stream()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());

        Assert.assertFalse("Account should be deleted after 'Yes'.",
                remainingLines.stream().anyMatch(line -> line.contains(String.valueOf(TEST_ACC_NO))));

        Assert.assertEquals("File should contain 2 lines after deletion.", 2, remainingLines.size());

        // Assert 2: Verify console prompt was printed
        Assert.assertTrue("Output should contain the confirmation prompt.",
                outputStream.toString().contains("Are you sure want to delete your account?"));

        // Assert 3: Verify Main.menu was NOT called (because confirmation was 'Yes')
        Assert.assertEquals("Menu method should NOT be called on successful deletion.",
                -1, deletionSpy.menuCallAccNo);
    }

    @Test
    public void testAccCloseFun_ConfirmationNo() throws IOException {
        // Arrange: Simulate "2" (No) confirmation input
        provideInput("2" + NL);

        // Act
        deletionSpy.accCloseFun(TEST_ACC_NO, CRED_FILE_NAME);

        // Assert 1: Check the file content (It should be UNCHANGED)
        // --- FIX: Read the file back and join with the same separator used in setUp ---
        String finalCredContent = Files.readAllLines(mockCredFile.toPath()).stream()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.joining(NL)); // Use NL (System.lineSeparator())

        Assert.assertEquals("File content should be identical to initial content after 'No' confirmation.",
                initialCredsExpected, finalCredContent);

        Assert.assertTrue("Account should NOT be deleted after 'No'.",
                finalCredContent.contains(String.valueOf(TEST_ACC_NO)));

        // Assert 2: Verify Main.menu was called
        Assert.assertEquals("Menu method should be called on 'No' confirmation.",
                TEST_ACC_NO, deletionSpy.menuCallAccNo);
    }

    @Test
    public void testAccCloseFun_InvalidInputThenYes() throws IOException {
        // --- FIX: Provide newline-separated input for the new nextLine() logic ---
        provideInput("9" + NL + "1" + NL);

        // Act
        deletionSpy.accCloseFun(TEST_ACC_NO, CRED_FILE_NAME);

        // Assert 1: Check the file content (The account should be deleted after the final '1')
        List<String> remainingLines = Files.readAllLines(mockCredFile.toPath()).stream()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());

        String finalContent = String.join(NL, remainingLines);

        Assert.assertFalse("Account should be deleted after final 'Yes'.",
                finalContent.contains(String.valueOf(TEST_ACC_NO)));

        Assert.assertEquals("File should contain 2 lines after deletion.", 2, remainingLines.size());

        // Assert 2: Verify the error message was printed
        String output = outputStream.toString();
        Assert.assertTrue("Output should contain the invalid option error message.",
                output.contains("Incorrect! Choose a valid option again.\n"));

        // Assert 3: Verify Main.menu was NOT called
        Assert.assertEquals("Menu method should NOT be called after successful deletion.",
                -1, deletionSpy.menuCallAccNo);
    }
}