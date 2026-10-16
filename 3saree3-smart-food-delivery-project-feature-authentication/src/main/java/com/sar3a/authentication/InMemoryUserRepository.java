
package com.sar3a.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        if (user == null || user.getEmail() == null
                || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Valid user email is required.");
        }

        users.put(user.getEmail().trim(), user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(users.get(email.trim()));
    }
}

