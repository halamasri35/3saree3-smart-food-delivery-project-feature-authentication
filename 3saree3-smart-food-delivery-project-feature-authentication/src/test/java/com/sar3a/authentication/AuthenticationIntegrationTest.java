
package com.sar3a.authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationIntegrationTest {

    @Test
    void customerCanLoginUsingHashedPassword() {

        Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher();
        InMemoryUserRepository repository = new InMemoryUserRepository();

        User customer = new User(
                "customer@sar3a.com",
                hasher.hash("Customer123!"),
                UserRole.CUSTOMER
        );

        repository.save(customer);

        AuthenticationService service =
                new AuthenticationService(repository, hasher);

        LoginResult result =
                service.login("customer@sar3a.com", "Customer123!");

        assertTrue(result.isSuccess());
        assertEquals(UserRole.CUSTOMER, result.getRole());
    }

    @Test
    void wrongPasswordIsRejected() {

        Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher();
        InMemoryUserRepository repository = new InMemoryUserRepository();

        User customer = new User(
                "customer@sar3a.com",
                hasher.hash("Customer123!"),
                UserRole.CUSTOMER
        );

        repository.save(customer);

        AuthenticationService service =
                new AuthenticationService(repository, hasher);

        LoginResult result =
                service.login("customer@sar3a.com", "WrongPassword");

        assertFalse(result.isSuccess());
        assertNull(result.getRole());
    }
}
