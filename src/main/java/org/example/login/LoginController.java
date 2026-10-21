package org.example.login;

import org.example.Main.MainFrame;
import org.example.login.NewLogin.NewLoginController;
import org.example.login.NewLogin.NewLoginPopup;
import org.example.model.Employee;

import javax.swing.*;

public class LoginController {
    MainFrame mainFrame;

    public LoginController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public boolean attempt_login(String username, char[] password) {
        int result = LoginDao.validate_login(username, new String(password));
        if (result == -1) {
            JOptionPane.showMessageDialog(null, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            Object[] empData = LoginDao.get_emp(result);
            mainFrame.setCurrentEmployee(new Employee((int) empData[0], (String) empData[1], (String) empData[2]));
            switch ((String) empData[2]) {
                case "admin":
                    mainFrame.showAdmin();
                    if (username.equals("admin") || new String(password).equals("admin")) {
                        NewLoginPopup loginPopup = new NewLoginPopup(new NewLoginController(), (int) empData[0]);
                    }
                    break;
                case "doctor", "technician":
                    mainFrame.showEmployee();
                    break;
                default:
                    System.out.println("Invalid employee title");
                    break;
            }

            JOptionPane.showMessageDialog(null, "Login Successful");
        }
        return false;
    }
}
