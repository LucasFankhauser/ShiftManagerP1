package org.example.admin;

import org.example.Main.MainFrame;
import org.example.login.LoginCredentials;
import org.example.model.Employee;

import java.sql.SQLException;
import java.util.Arrays;

public class AdminController {
    private final MainFrame mainFrame;
    private final AdminDao adminDao = new AdminDao();
    private AdminPanel panel;

    public AdminController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void initialize() {
        panel = mainFrame.getAdminPanel();
        loadEmployees();
    }

    public void loadEmployees() {
        if (!requireAdmin()) {
            return;
        }

        try {
            var employees = adminDao.findEmployees();
            panel.setEmployees(employees);
            panel.setStatus(employees.size() + " employees found");
        } catch (SQLException exception) {
            panel.showError("Could not load employees: " + databaseMessage(exception));
        }
    }

    public void createEmployee() {
        if (!requireAdmin()) {
            return;
        }

        String name = panel.getEmployeeName().trim();
        String title = panel.getEmployeeTitle();
        String username = panel.getUsername().trim();
        char[] password = panel.getPassword();

        try {
            if (name.isBlank()) {
                throw new IllegalArgumentException("Employee name cannot be blank.");
            }
            if (!"doctor".equals(title) && !"technician".equals(title)) {
                throw new IllegalArgumentException(
                        "Only doctors and technicians can be created."
                );
            }
            LoginCredentials.validate(username, password);

            adminDao.createEmployee(name, title, username, new String(password));
            panel.clearCreateForm();
            loadEmployees();
            panel.setStatus(name + " was created.");
        } catch (IllegalArgumentException exception) {
            panel.showError(exception.getMessage());
        } catch (SQLException exception) {
            panel.showError("Could not create employee: " + databaseMessage(exception));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public void removeSelected() {
        if (!requireAdmin()) {
            return;
        }

        Integer employeeId = panel.getSelectedEmployeeId();
        String employeeName = panel.getSelectedEmployeeName();
        if (employeeId == null) {
            panel.showError("Select an employee to remove.");
            return;
        }
        if (!panel.confirmRemoval(employeeName)) {
            return;
        }

        try {
            if (!adminDao.removeEmployee(employeeId)) {
                panel.showError("The selected employee no longer exists.");
                return;
            }
            loadEmployees();
            panel.setStatus(employeeName + " was removed.");
        } catch (SQLException exception) {
            panel.showError("Could not remove employee: " + databaseMessage(exception));
        }
    }

    public void setPassword() {
        if (!requireAdmin()) {
            return;
        }

        Integer employeeId = panel.getSelectedEmployeeId();
        if (employeeId == null) {
            panel.showError("Select an employee before setting a password.");
            return;
        }
        String employeeName = panel.getSelectedEmployeeName();

        char[] password = panel.getResetPassword();
        try {
            // Password resets do not change the username, so validate with a
            // harmless non-admin placeholder for the username rule.
            LoginCredentials.validate("employee", password);
            if (!adminDao.updatePassword(employeeId, new String(password))) {
                panel.showError("That employee does not have login data.");
                return;
            }
            panel.clearResetPassword();
            loadEmployees();
            panel.setStatus("Password updated for " + employeeName + ".");
        } catch (IllegalArgumentException exception) {
            panel.showError(exception.getMessage());
        } catch (SQLException exception) {
            panel.showError("Could not set password: " + databaseMessage(exception));
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public void logout() {
        mainFrame.logout();
    }

    private boolean requireAdmin() {
        Employee employee = mainFrame.getCurrentEmployee();
        if (employee != null && "admin".equals(employee.getTitle())) {
            return true;
        }

        if (panel != null) {
            panel.showError("Only the system administrator may perform this action.");
        }
        return false;
    }

    private String databaseMessage(SQLException exception) {
        if ("23505".equals(exception.getSQLState())) {
            return "that username is already taken.";
        }
        return exception.getMessage();
    }
}
