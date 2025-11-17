package org.example;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;


@RunWith(JUnit4.class)
public class LoginAuthTest {

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

        File dbDir = tempFolder.newFolder("db");
        mockCredentialsFile = new File(dbDir, "credentials.txt");

        try (FileWriter writer = new FileWriter(mockCredentialsFile)) {
            writer.write(VALID_ACC_NO + " " + VALID_PASS + "\n");
            writer.write("67890 otherpass\n");
        }

        loginSpy = new LoginSpy();
        loginSpy.setFile(mockCredentialsFile);
        loginSpy.resetSpy();
    }

    @After
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private static class LoginSpy extends Login {

        boolean loginFunCalled = false;

        @Override
        void loginFun() throws IOException {
                loginFunCalled = true;
        }

        public void resetSpy() {

            loginFunCalled = false;

        }
    }

    @Test
    public void testSuccessfulLogin_LoginFun() throws IOException {
        loginSpy.setCredentials(Integer.parseInt(VALID_ACC_NO), VALID_PASS);

        loginSpy.loginAuth();

        String actualOutput = outputStream.toString();

        Assert.assertTrue("Output should contain 'Login Successful!!'",
                actualOutput.contains("Login Successful!!\n"));

    }

    @Test
    public void testIncorrectPassword_LoginFun() throws IOException {
        loginSpy.setCredentials(Integer.parseInt(VALID_ACC_NO), INCORRECT_PASS);

        loginSpy.loginAuth();

        String actualOutput = outputStream.toString();

        Assert.assertTrue("Output should contain 'Incorrect Password!'",
                actualOutput.contains("\nIncorrect Password!"));

    }

    @Test
    public void testAccountDoesNotExist_LoginAuth() throws IOException {
        String NON_EXISTENT_ACC_NO = "99999";
        String ANY_PASS = "test";

        loginSpy.setCredentials(Integer.parseInt(NON_EXISTENT_ACC_NO), ANY_PASS);

        loginSpy.loginAuth();

        String actualOutput = outputStream.toString();

        Assert.assertTrue("'Account doesn't exists!'",
                actualOutput.contains("\nAccount doesn't exists!"));

    }
}
