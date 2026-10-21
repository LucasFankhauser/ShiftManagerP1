package org.example.login.NewLogin;

import javax.swing.*;

public class NewLoginController {
    boolean validateNewLogin(int emp, String username, char[] password) {
        if (username.equals("admin") || new String(password).equals("admin")) {
            JOptionPane.showMessageDialog(null, "Username or Password can not be admin", "Invalid Login", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (username == "" || new String(password).equals("")) {
            JOptionPane.showMessageDialog(null, "Username or Password can not be blank", "Invalid Login", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
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
        }
    }
}
