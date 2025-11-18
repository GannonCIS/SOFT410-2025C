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
public class CreationTest {

    // Rule for creating and automatically cleaning up temporary files/folders
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private File dbDir;
    private File mockCredentialsFile;
    private File mockBalanceFile;
    private File mockUserFile;
    private CreationSpy creationSpy;

    // --- Test Data ---
    private static final String FIRST_NAME = "Leia";
    private static final String LAST_NAME = "Organa";
    private static final String DOB = "1977-05-25";
    private static final String GENDER = "Female";
    private static final String ADDRESS = "123_Rebel_Base";
    private static final String PHONE = "5551234567";
    private static final String EMAIL = "leia@alderaan.com";
    private static final String ID_NO = "R7705";
    private static final String PASSWORD = "lightspeed";

    // Concatenated input string for the user interaction sequence
    private static final String VALID_INPUT = String.join(System.lineSeparator(),
            FIRST_NAME + " " + LAST_NAME,
            DOB,
            GENDER,
            ADDRESS,
            PHONE,
            EMAIL,
            ID_NO,
            PASSWORD
    );

    @Before
    public void setUp() throws IOException {
        // 1. Setup I/O redirection
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // 2. Setup mock database structure inside the temporary folder
        dbDir = tempFolder.newFolder("db");
        mockCredentialsFile = new File(dbDir, "credentials.txt");
        mockBalanceFile = new File(dbDir, "balanceDB.txt");
        mockUserFile = new File(dbDir, "userDB.txt");

        // Initialize files (must exist for file path checks)
        mockCredentialsFile.createNewFile();
        mockBalanceFile.createNewFile();
        mockUserFile.createNewFile();

        // 3. Initialize the spy object
        creationSpy = new CreationSpy(dbDir.getAbsolutePath());
    }

    @After
    public void tearDown() {
        // Restore original System streams
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Spy class to intercept all hardcoded file paths and the call to Main.menu().
     */
    private class CreationSpy extends Creation {
        private String dbPath;
        public int menuCallAccNo = -1;

        public CreationSpy(String tempPath) {
            this.dbPath = tempPath;
        }

        // Override file writing methods to use temporary paths
        @Override
        void credWrite(int accNo, String[] accLineInfo) throws IOException {
            FileWriter writer = new FileWriter(dbPath + "/credentials.txt", true);
            writer.write("\n" + accNo + " " + accLineInfo[8]);
            writer.close();
        }

        @Override
        void balWrite(int accNo) throws IOException {
            int initialBal = 69;
            FileWriter writer = new FileWriter(dbPath + "/balanceDB.txt", true);
            writer.write("\n" + accNo + " " + initialBal);
            writer.close();
        }

        @Override
        void userWrite(int accNo, String[] accLineInfo) throws IOException {
            FileWriter writer = new FileWriter(dbPath + "/userDB.txt", true);
            writer.write("\n" + accNo + " ");
            for (int i = 0; i < 8; i++) {
                writer.write(accLineInfo[i] + " ");
            }
            writer.close();
        }

        @Override
        int accNoCreation() throws IOException {
            String lastLine = "";
            int accNo = 1; // Default to 1
            File file = new File(dbPath + "/credentials.txt"); // Use spy path

            if (file.length() > 0) { // Check if the file is truly empty
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    lastLine = scanner.nextLine();
                }
                scanner.close();

                if (!lastLine.trim().isEmpty()) {
                    String[] subLine = lastLine.split(" ");
                    accNo = Integer.parseInt(subLine[0]) + 1;
                }
            }
            return accNo;
        }

        // Intercept the static call to Main.menu()
        @Override
        void menuCall(int accNo) {
            this.menuCallAccNo = accNo;
        }
    }

    // --- Test Cases ---

    @Test
    public void testFullAccountCreation_InitialAccount() throws IOException {
        // Arrange: Provide all simulated user input
        System.setIn(new ByteArrayInputStream(VALID_INPUT.getBytes()));

        // Act
        creationSpy.createAccFun();

        final int expectedAccNo = 1;

        // Assert 1: Verify all three files were written with correct data
        String credContent = Files.readString(mockCredentialsFile.toPath()).trim();
        String balContent = Files.readString(mockBalanceFile.toPath()).trim();
        String userContent = Files.readString(mockUserFile.toPath()).trim();

        Assert.assertTrue("Credentials file should contain the account number and password.",
                credContent.contains(expectedAccNo + " " + PASSWORD));

        Assert.assertTrue("Balance file should contain the account number and initial balance (69).",
                balContent.contains(expectedAccNo + " 69"));

        Assert.assertTrue("User file should contain the full user details.",
                userContent.contains(expectedAccNo + " " + FIRST_NAME + " " + LAST_NAME + " " + DOB + " " + GENDER));

        // Assert 2: Verify console output confirmation
        String actualOutput = outputStream.toString();
        Assert.assertTrue("Output should confirm success.",
                actualOutput.contains("Account created successfully!"));
        Assert.assertTrue("Output should show the new account number.",
                actualOutput.contains("Your account number is: " + expectedAccNo));

        // Assert 3: Verify Main.menu was intercepted with the correct account number
        Assert.assertEquals("The spy method should have been called with the new account number.",
                expectedAccNo, creationSpy.menuCallAccNo);
    }

    @Test
    public void testAccNoCreation_NextSequentialNumber() throws IOException {
        // Arrange: Pre-populate the credentials file with a high account number
        final int lastAccNo = 80000;
        try (FileWriter writer = new FileWriter(mockCredentialsFile)) {
            writer.write(lastAccNo + " existingpass\n");
        }

        // Act
        int nextAccNo = creationSpy.accNoCreation();

        // Assert
        Assert.assertEquals("The next account number should be lastAccNo + 1.",
                lastAccNo + 1, nextAccNo);
    }

    @Test
    public void testGetUserInfoFromUser_RequiresBothNames() throws IOException {
        // Arrange: Simulate invalid input (single name), then valid input
        String input = String.join(System.lineSeparator(),
                "Leia", // Invalid: Missing last name
                VALID_INPUT
        );
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Act
        String[] info = creationSpy.getUserInfoFromUser();

        // Assert 1: Check that the method successfully returned the correct final info
        Assert.assertEquals(PASSWORD, info[8]);

        // Assert 2: Check that the error message was printed
        String actualOutput = outputStream.toString();
        Assert.assertTrue("Output should prompt for both names after invalid input.",
                actualOutput.contains("Please provide both first name and last name."));
    }
}