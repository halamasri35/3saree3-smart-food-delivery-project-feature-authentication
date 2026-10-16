package com.sar3a.authentication;

public class LoginResult {

    private final boolean success;
    private final String message;
    private final UserRole role;

    public LoginResult(boolean success, String message, UserRole role) {
        this.success = success;
        this.message = message;
        this.role = role;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserRole getRole() {
        return role;
    }
}