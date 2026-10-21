package org.example.login;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

//DB_URL=jdbc:postgresql://ambari-node5.csc.calpoly.edu:5432/employee_management DB_USER=LSNProject DB_PASSWORD=LSN
public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPanel(LoginController controller) {
        setLayout(new GridBagLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        form.add(new JLabel());
        form.add(loginButton);

        add(form);

        loginButton.addActionListener(e -> {
            controller.attempt_login(
                    usernameField.getText(),
                    passwordField.getPassword()
            );
        });
    }
}