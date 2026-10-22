package org.example.login.NewLogin;

import org.example.login.LoginCredentials;

import javax.swing.*;
import java.util.Arrays;

public class NewLoginController {
    boolean validateNewLogin(int emp, String username, char[] password) {
        try {
            username = username == null ? "" : username.trim();
            if (username.isBlank()) {
                throw new IllegalArgumentException(
                        "Username cannot be blank."
                );
            }

            String newPassword = new String(password);

            if (newPassword.isBlank()) {
                throw new IllegalArgumentException(
                        "The administrator's new password cannot be blank."
                );
            }

            if (newPassword.equals("admin")) {
                throw new IllegalArgumentException(
                        "The new password cannot be admin."
                );
            }

            return NewLoginDao.create_login(
                    emp,
                    username,
                    new String(password)
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Invalid Login",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "An unexpected database error occurred.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
