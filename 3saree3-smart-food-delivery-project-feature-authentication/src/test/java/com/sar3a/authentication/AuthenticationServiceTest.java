package com.sar3a.authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class AuthenticationServiceTest {

    private final User testUser =
            new User("customer@test.com", "test-hash", UserRole.CUSTOMER);

    private final UserRepository repository = email ->
            email.equals("customer@test.com")
                    ? Optional.of(testUser)
                    : Optional.empty();

    private final PasswordVerifier verifier =
            (rawPassword, storedHash) ->
                    rawPassword.equals("correct123")
                            && storedHash.equals("test-hash");

    private final AuthenticationService service =
            new AuthenticationService(repository, verifier);

    @Test
    void loginWithValidCredentialsShouldSucceed() {
        LoginResult result = service.login(
                "customer@test.com", "correct123");

        assertTrue(result.isSuccess());
        assertEquals(UserRole.CUSTOMER, result.getRole());
        assertEquals("Login successful.", result.getMessage());
    }

    @Test
    void loginWithWrongPasswordShouldFail() {
        LoginResult result = service.login(
                "customer@test.com", "wrong123");

        assertFalse(result.isSuccess());
        assertNull(result.getRole());
    }

    @Test
    void loginWithUnknownEmailShouldFail() {
        LoginResult result = service.login(
                "unknown@test.com", "correct123");

        assertFalse(result.isSuccess());
        assertEquals("Invalid email or password.", result.getMessage());
    }

    @Test
    void loginWithEmptyCredentialsShouldFail() {
        LoginResult result = service.login("", "");

        assertFalse(result.isSuccess());
        assertEquals("Email and password are required.", result.getMessage());
    }
    @Test
void loginAsManagerShouldSucceed() {

    User manager = new User(
            "manager@test.com",
            "manager-hash",
            UserRole.MANAGER
    );

    UserRepository managerRepository = email ->
            email.equals("manager@test.com")
                    ? Optional.of(manager)
                    : Optional.empty();

    PasswordVerifier managerVerifier =
            (password, hash) ->
                    password.equals("manager123")
                            && hash.equals("manager-hash");

    AuthenticationService managerService =
            new AuthenticationService(
                    managerRepository,
                    managerVerifier
            );

    LoginResult result = managerService.login(
            "manager@test.com",
            "manager123"
    );

    assertTrue(result.isSuccess());
    assertEquals(UserRole.MANAGER, result.getRole());
}
}