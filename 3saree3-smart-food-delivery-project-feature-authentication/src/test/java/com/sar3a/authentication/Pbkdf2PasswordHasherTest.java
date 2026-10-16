
package com.sar3a.authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Pbkdf2PasswordHasherTest {

    private final Pbkdf2PasswordHasher hasher =
            new Pbkdf2PasswordHasher();

    @Test
    void correctPasswordShouldMatch() {
        String hash = hasher.hash("MySecurePassword123!");

        assertTrue(hasher.matches("MySecurePassword123!", hash));
    }

    @Test
    void wrongPasswordShouldNotMatch() {
        String hash = hasher.hash("MySecurePassword123!");

        assertFalse(hasher.matches("WrongPassword", hash));
    }

    @Test
    void samePasswordShouldProduceDifferentHashes() {
        String firstHash = hasher.hash("SamePassword123!");
        String secondHash = hasher.hash("SamePassword123!");

        assertNotEquals(firstHash, secondHash);
    }

    @Test
    void invalidStoredHashShouldFail() {
        assertFalse(hasher.matches("password", "invalid-hash"));
    }
}
