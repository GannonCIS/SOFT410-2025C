package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UserRepository {

    public interface PathResolver {
        String getFilePath(String fileName);
    }

    private final PathResolver pathResolver;
    private static final String USER_DB_PATH = "db/userDB.txt";

    public UserRepository(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public Account getAccountByAccNo(int accNo) throws FileNotFoundException {
        String filePath = pathResolver.getFilePath(USER_DB_PATH);
        File file = new File(filePath);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] detail = line.split(" ");
                if (detail.length >= 9) {
                    try {
                        if (accNo == Integer.parseInt(detail[0])) {
                            int foundAccNo = Integer.parseInt(detail[0]);
                            String firstName = detail[1];
                            String lastName = detail[2];
                            String dob = detail[3];
                            String gender = detail[4];
                            String address = detail[5];
                            String phoneNumber = detail[6];
                            String email = detail[7];
                            String citizenshipNumber = detail[8];
                            String passwordPlaceholder = ""; // Not stored in userDB.txt

                            return new Account(
                                    foundAccNo, firstName, lastName, dob, gender, address,
                                    phoneNumber, email, citizenshipNumber, passwordPlaceholder
                            );
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null; // Account not found
    }
}