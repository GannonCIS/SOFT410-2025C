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
import java.util.Scanner;


@RunWith(JUnit4.class)
public class loginAuthTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    private File mockCredentialsFile;
    private LoginSpy loginSpy;

    private static final String VALID_ACC_NO = "12345";
    private static final String VALID_PASS = "secure123";
    private static final String INCORRECT_PASS = "wrongpass";

    @Before
    public void setUp() throws IOException {

        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Create the mock file in the temporary folder
        File dbDir = tempFolder.newFolder("db");
        mockCredentialsFile = new File(dbDir, "credentials.txt");

        try (FileWriter writer = new FileWriter(mockCredentialsFile)) {
            writer.write(VALID_ACC_NO + " " + VALID_PASS + "\n");
            writer.write("67890 otherpass\n");
        }

        // Initialize the Spy and set its file path
        loginSpy = new LoginSpy();
        loginSpy.file = mockCredentialsFile; // Manually set the file path
    }

    @After
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Spy class to intercept recursive calls (loginFun)
     * and application flow calls (menuCall).
     */
    private static class LoginSpy extends Login {

        public int menuCallAccNo = -1;
        public boolean loginFunCalled = false;

        // This method is called by the test to set private fields
        public void setCredentials(int accNo, String pass) {
            // We can't access private fields, so we assume Login.java
            // makes accNo and pass protected or has setters.
            // Since it doesn't, we must modify the spy to use the protected fields.
            // Wait, the fields accNo and pass *are* accessible in the spy.
            super.accNo = accNo;
            super.pass = pass;
        }

        // Intercepts the recursive call on failure
        @Override
        void loginFun() throws IOException {
            loginFunCalled = true;
        }

        // Intercepts the Main.menu() call on success
        @Override
        void menuCall(int accNo) throws IOException {
            this.menuCallAccNo = accNo;
        }
    }

    @Test
    public void testSuccessfulLogin() throws IOException {
        // Arrange: Set the credentials the loginAuth() method will use
        loginSpy.setCredentials(Integer.parseInt(VALID_ACC_NO), VALID_PASS);

        // Act
        loginSpy.loginAuth();

        // Assert
        String actualOutput = outputStream.toString();
        Assert.assertTrue("Output should contain 'Login Successful!!'",
                actualOutput.contains("Login Successful!!\n"));

        // Assert that the menu interceptor was called, not the real Main.menu()
        Assert.assertEquals("Menu call interceptor should be called with the correct account number.",
                Integer.parseInt(VALID_ACC_NO), loginSpy.menuCallAccNo);
    }

    @Test
    public void testIncorrectPassword() throws IOException {
        loginSpy.setCredentials(Integer.parseInt(VALID_ACC_NO), INCORRECT_PASS);

        loginSpy.loginAuth();

        String actualOutput = outputStream.toString();
        Assert.assertTrue("Output should contain 'Incorrect Password!'",
                actualOutput.contains("\nIncorrect Password!"));

        // Assert that the recursive loginFun() interceptor was called
        Assert.assertTrue("loginFun() interceptor should have been called on failure.",
                loginSpy.loginFunCalled);
    }

    @Test
    public void testAccountDoesNotExist() throws IOException {
        String NON_EXISTENT_ACC_NO = "99999";
        String ANY_PASS = "test";

        loginSpy.setCredentials(Integer.parseInt(NON_EXISTENT_ACC_NO), ANY_PASS);

        loginSpy.loginAuth();

        String actualOutput = outputStream.toString();
        Assert.assertTrue("Output should contain 'Account doesn't exists!'",
                actualOutput.contains("\nAccount doesn't exists!"));

        // Assert that the recursive loginFun() interceptor was called
        Assert.assertTrue("loginFun() interceptor should have been called on failure.",
                loginSpy.loginFunCalled);
    }
}