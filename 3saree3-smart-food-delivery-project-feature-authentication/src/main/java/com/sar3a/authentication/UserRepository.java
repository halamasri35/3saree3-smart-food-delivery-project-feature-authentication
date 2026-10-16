package com.sar3a.authentication;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

}