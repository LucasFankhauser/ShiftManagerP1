package org.example.login;

public final class LoginCredentials {
    private LoginCredentials() {}

    public static void validate(String username, char[] password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank.");
        }

        String enteredPassword = password == null ? "" : new String(password);
        if (username.trim().equalsIgnoreCase("admin")
                || enteredPassword.equals("admin")) {
            throw new IllegalArgumentException(
                    "Username and password cannot be admin."
            );
        }
    }
}
