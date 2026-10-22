package org.example.login;

public final class LoginCredentials {
    private LoginCredentials() {}

    public static void validate(String username, char[] password) {
        if (username == null || username.isBlank()
                || password == null || password.length == 0
                || new String(password).isBlank()) {
            throw new IllegalArgumentException(
                    "Username and password cannot be blank."
            );
        }

        if (username.trim().equalsIgnoreCase("admin")
                || new String(password).equals("admin")) {
            throw new IllegalArgumentException(
                    "Username and password cannot be admin."
            );
        }
    }
}
