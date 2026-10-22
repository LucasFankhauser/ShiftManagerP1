package org.example.login;

import org.example.Main.MainFrame;
import org.example.login.NewLogin.NewLoginController;
import org.example.login.NewLogin.NewLoginPopup;
import org.example.model.Employee;

import javax.swing.*;
import java.util.Arrays;

public class LoginController {
    MainFrame mainFrame;

    public LoginController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public boolean attempt_login(String username, char[] password) {
        String enteredUsername = username == null ? "" : username.trim();
        String enteredPassword = new String(password);

        try {
            if (enteredUsername.isBlank()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Username cannot be blank.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            int result = LoginDao.validate_login(
                    enteredUsername, enteredPassword
            );
            if (result == -1) {
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Object[] empData = LoginDao.get_emp(result);
            if (empData == null) {
                JOptionPane.showMessageDialog(
                        null, "No employee is associated with this login.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            Employee employee = new Employee(
                    (int) empData[0],
                    (String) empData[1],
                    (String) empData[2]
            );

            switch (employee.getTitle()) {
                case "admin" -> {
                    if (enteredUsername.equals("admin")
                            && enteredPassword.equals("admin")) {
                        new NewLoginPopup(
                                new NewLoginController(),
                                employee.getId(),
                                enteredUsername
                        );
                    }
                    mainFrame.setCurrentEmployee(employee);
                    mainFrame.showAdmin();
                }
                case "doctor", "technician" -> {
                    mainFrame.setCurrentEmployee(employee);
                    mainFrame.showEmployee();
                }
                default -> {
                    JOptionPane.showMessageDialog(
                            null, "Invalid employee title.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE
                    );
                    return false;
                }
            }

            JOptionPane.showMessageDialog(null, "Login Successful");
            return true;
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
