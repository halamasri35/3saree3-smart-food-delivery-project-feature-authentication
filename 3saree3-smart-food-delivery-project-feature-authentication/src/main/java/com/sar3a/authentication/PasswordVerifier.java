package com.sar3a.authentication;

public interface PasswordVerifier {

    boolean matches(String rawPassword, String storedPasswordHash);

}