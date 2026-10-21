package org.example.Main;

import org.example.admin.AdminController;
import org.example.admin.AdminPanel;
import org.example.employee.EmployeeController;
import org.example.employee.EmployeePanel;
import org.example.login.LoginPanel;
import org.example.login.LoginController;
import org.example.model.Employee;

import javax.swing.*;

public class MainFrame extends JFrame {
    // Current Logging In Employee
    Employee currentEmployee;

    // Login
    LoginPanel loginPanel;
    LoginController loginController;

    // Employee
    EmployeePanel employeePanel;
    EmployeeController employeeController;

    // Admin
    AdminPanel adminPanel;
    AdminController adminController;

    public MainFrame() {

        // Login
        loginController = new LoginController(this);
        loginPanel = new LoginPanel(loginController);


//         Employee
        employeeController = new EmployeeController(this);
        employeePanel = new EmployeePanel(employeeController);


        // Admin
        //adminController = new AdminController(this);
        //adminPanel = new AdminPanel(adminController);

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
        System.out.println("Will show admin once implemented");
        //showPanel(adminPanel);
    }

    public void showEmployee() {
        System.out.println("Will show employee once implemented");


        showPanel(employeePanel);
    }

    public void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
    public void setCurrentEmployee(Employee emp) {
        currentEmployee = emp;
        employeeController.initialize();

    }

    public Employee getCurrentEmployee()
    {
        return currentEmployee;
    }

    public EmployeePanel getEmployeePanel()
    {
        return employeePanel;
    }
}