
package com.sar3a.authentication;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Pbkdf2PasswordHasher implements PasswordVerifier {

    private static final int ITERATIONS = 600_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private final SecureRandom secureRandom = new SecureRandom();

    public String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = deriveKey(password, salt, ITERATIONS);

        return "pbkdf2-sha256:"
                + ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean matches(String rawPassword, String storedPasswordHash) {
        if (rawPassword == null || storedPasswordHash == null) {
            return false;
        }

        try {
            String[] parts = storedPasswordHash.split(":", -1);

            if (parts.length != 4 ||
                    !parts[0].equals("pbkdf2-sha256")) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            if (iterations < 1 || iterations > 2_000_000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            if (salt.length != SALT_LENGTH ||
                    expectedHash.length != KEY_LENGTH / 8) {
                return false;
            }

            byte[] actualHash = deriveKey(
                    rawPassword, salt, iterations);

            return MessageDigest.isEqual(actualHash, expectedHash);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] deriveKey(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Password hashing failed.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
