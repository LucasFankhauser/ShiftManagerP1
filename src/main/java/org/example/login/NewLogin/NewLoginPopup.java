package org.example.login.NewLogin;

import javax.swing.*;
import java.awt.*;

public class NewLoginPopup {
    NewLoginController newLoginController;
    int emp;

    public NewLoginPopup(NewLoginController _newLoginController, int _emp) {
        newLoginController = _newLoginController;
        emp = _emp;
        boolean res = newLogin();
        while (res == false) {
            res = newLogin();
        }
    }

    public boolean newLogin() {

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));

        panel.add(new JLabel("New Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("New Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showOptionDialog(
                null,
                panel,
                "Create New Login",
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
