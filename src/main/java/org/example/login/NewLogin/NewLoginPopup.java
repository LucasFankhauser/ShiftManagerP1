package org.example.login.NewLogin;

import javax.swing.*;
import java.awt.*;

public class NewLoginPopup {
    private final NewLoginController newLoginController;
    private final int emp;
    private final String currentUsername;

    public NewLoginPopup(
            NewLoginController newLoginController,
            int emp,
            String currentUsername
    ) {
        this.newLoginController = newLoginController;
        this.emp = emp;
        this.currentUsername = currentUsername;

        while (!newLogin()) {
            // The default password must be changed before continuing.
        }
    }

    public boolean newLogin() {

        JTextField usernameField = new JTextField(currentUsername, 15);
        JPasswordField passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("The default admin login must be changed."));
        panel.add(new JLabel());

        panel.add(new JLabel("New Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("New Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showOptionDialog(
                null,
                panel,
                "Change Default Administrator Login",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{"OK"},
                "OK"
        );

        if (result == 0) {
            return newLoginController.validateNewLogin(emp, usernameField.getText(), passwordField.getPassword());
        }
        return false;
    }
}
