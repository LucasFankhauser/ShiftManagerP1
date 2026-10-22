package org.example.Main;

import org.example.admin.AdminController;
import org.example.admin.AdminPanel;
import org.example.admin.schedule.ScheduleGenerator;
import org.example.employee.EmployeeController;
import org.example.employee.EmployeePanel;
import org.example.login.LoginPanel;
import org.example.login.LoginController;
import org.example.model.Employee;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Date;
import org.example.employee.request.RequestController;
import org.example.employee.request.RequestPane;
import org.example.model.RequestKind;
import org.example.model.ShiftChoice;

import java.util.List;

public class MainFrame extends JFrame {
    // Current Logging In Employee
    Employee currentEmployee;

    // Login
    LoginPanel loginPanel;
    LoginController loginController;

    // Employee
    EmployeePanel employeePanel;
    EmployeeController employeeController;
    RequestPane requestPane;
    RequestController requestController;

    // Admin
    AdminPanel adminPanel;
    AdminController adminController;

    public MainFrame() {
//        ScheduleGenerator.drop_all_shifts();
//        ScheduleGenerator.generate_schedule(LocalDate.of(2026, 10, 19), LocalDate.of(2026, 11, 1));

        // Login
        loginController = new LoginController(this);
        loginPanel = new LoginPanel(loginController);


//         Employee
        employeeController = new EmployeeController(this);

        requestPane = new RequestPane();

        requestPane.setShiftChoices(List.of(
                new ShiftChoice(1, "Early Morning"),
                new ShiftChoice(2, "8:30 Morning"),
                new ShiftChoice(3, "Late Doctor"),
                new ShiftChoice(4, "Surgery"),
                new ShiftChoice(5, "Night Shift")
        ));

        requestController = new RequestController(
                this,
                requestPane,
                employeeController
        );

        employeePanel = new EmployeePanel(
                employeeController,
                requestPane
        );



        // Admin
        adminController = new AdminController(this);
        adminPanel = new AdminPanel(adminController);

        setTitle("Employee Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showLogin();

        setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    public void showLogin() {
        showPanel(loginPanel);
    }

    public void showAdmin() {
        showPanel(adminPanel);
        adminController.initialize();
    }

    public void showEmployee() {
        showPanel(employeePanel);
        employeeController.initialize();
    }

    public void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
    public void setCurrentEmployee(Employee emp) {
        currentEmployee = emp;
    }

    public Employee getCurrentEmployee()
    {
        return currentEmployee;
    }

    public EmployeePanel getEmployeePanel()
    {
        return employeePanel;
    }

    public AdminPanel getAdminPanel()
    {
        return adminPanel;
    }

    public void logout()
    {
        currentEmployee = null;
        showLogin();
    }
}
