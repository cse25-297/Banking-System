
package model;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();
            spec.clearPassword();

            return "PBKDF2$" + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Password hashing is unavailable.", e
            );
        }
    }

    public static boolean verifyPassword(
            String password, String storedHash) {

        try {
            if (storedHash == null) {
                return false;
            }

            // Support existing SHA-256 passwords.
            if (!storedHash.startsWith("PBKDF2$")) {
                return MessageDigest.isEqual(
                        sha256(password).getBytes(),
                        storedHash.getBytes()
                );
            }

            String[] parts = storedHash.split("\\$");

            if (parts.length != 4) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash =
                    Base64.getDecoder().decode(parts[3]);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    expectedHash.length * 8
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] actualHash = factory.generateSecret(spec).getEncoded();
            spec.clearPassword();

            return MessageDigest.isEqual(actualHash, expectedHash);

        } catch (Exception e) {
            return false;
        }
    }

    private static String sha256(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(
                java.nio.charset.StandardCharsets.UTF_8
        ));

        StringBuilder result = new StringBuilder();

        for (byte b : hash) {
            result.append(String.format("%02x", b & 0xff));
        }

        return result.toString();
    }
}
