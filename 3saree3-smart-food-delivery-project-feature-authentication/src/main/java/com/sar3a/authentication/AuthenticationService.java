package com.sar3a.authentication;

import java.util.Optional;

public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier) {

        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
    }

    public LoginResult login(String email, String password) {

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return new LoginResult(
                    false,
                    "Email and password are required.",
                    null
            );
        }

        Optional<User> userOptional =
                userRepository.findByEmail(email.trim());

        if (userOptional.isEmpty()) {
            return new LoginResult(
                    false,
                    "Invalid email or password.",
                    null
            );
        }

        User user = userOptional.get();

        boolean passwordCorrect = passwordVerifier.matches(
                password,
                user.getPasswordHash()
        );

        if (!passwordCorrect) {
            return new LoginResult(
                    false,
                    "Invalid email or password.",
                    null
            );
        }

        return new LoginResult(
                true,
                "Login successful.",
                user.getRole()
        );
    }
}