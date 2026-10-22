package org.example.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Title", "Username", "Password"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable employeeTable = new JTable(tableModel);

    private final JTextField nameField = new JTextField(14);
    private final JComboBox<String> titleBox = new JComboBox<>(
            new String[]{"doctor", "technician"}
    );
    private final JTextField usernameField = new JTextField(14);
    private final JPasswordField passwordField = new JPasswordField(14);
    private final JTextField resetUsernameField = new JTextField(14);
    private final JPasswordField resetPasswordField = new JPasswordField(14);

    private final JButton createButton = new JButton("Create employee");
    private final JButton removeButton = new JButton("Remove selected");
    private final JButton credentialsButton = new JButton("Update login");
    private final JButton generateScheduleButton = new JButton("Generate Schedule");
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton logoutButton = new JButton("Log out");
    private final JLabel statusLabel = new JLabel(" ");

    public AdminPanel(AdminController controller) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("System administration"), BorderLayout.WEST);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerButtons.add(generateScheduleButton);
        headerButtons.add(refreshButton);
        headerButtons.add(logoutButton);
        header.add(headerButtons, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        JPanel forms = new JPanel();
        forms.setLayout(new BoxLayout(forms, BoxLayout.Y_AXIS));
        forms.add(createForm());
        forms.add(Box.createVerticalStrut(8));
        forms.add(credentialsForm());
        forms.add(Box.createVerticalStrut(5));
        forms.add(statusLabel);
        add(forms, BorderLayout.SOUTH);

        createButton.addActionListener(event -> controller.createEmployee());
        removeButton.addActionListener(event -> controller.removeSelected());
        credentialsButton.addActionListener(event -> controller.setCredentials());
        generateScheduleButton.addActionListener(event -> controller.generateSchedule());
        refreshButton.addActionListener(event -> controller.loadEmployees());
        logoutButton.addActionListener(event -> controller.logout());

        employeeTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String username = getSelectedUsername();
                resetUsernameField.setText(username == null ? "" : username);
                resetPasswordField.setText("");
            }
        });
    }

    private JPanel createForm() {
        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Create doctor or technician"
                )
        );

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets = new Insets(4, 5, 4, 5);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Name
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        panel.add(new JLabel("Name:"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(nameField, constraints);

        // Title
        constraints.gridx = 2;
        constraints.weightx = 0;
        panel.add(new JLabel("Title:"), constraints);

        constraints.gridx = 3;
        constraints.weightx = 1;
        panel.add(titleBox, constraints);

        // Username
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        panel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(usernameField, constraints);

        // Password
        constraints.gridx = 2;
        constraints.weightx = 0;
        panel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 3;
        constraints.weightx = 1;
        panel.add(passwordField, constraints);

        // Buttons
        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 5, 0)
        );

        buttons.add(createButton);
        buttons.add(removeButton);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(buttons, constraints);

        return panel;
    }

    private JPanel credentialsForm() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Update login for selected employee"
        ));
        panel.add(new JLabel("New username:"));
        panel.add(resetUsernameField);
        panel.add(new JLabel("New password:"));
        panel.add(resetPasswordField);
        panel.add(credentialsButton);
        return panel;
    }

    public String getEmployeeName() {
        return nameField.getText();
    }

    public String getEmployeeTitle() {
        return (String) titleBox.getSelectedItem();
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public char[] getResetPassword() {
        return resetPasswordField.getPassword();
    }

    public String getResetUsername() {
        return resetUsernameField.getText();
    }

    public Integer getSelectedEmployeeId() {
        int row = employeeTable.getSelectedRow();
        return row < 0 ? null : (Integer) tableModel.getValueAt(row, 0);
    }

    public String getSelectedEmployeeName() {
        int row = employeeTable.getSelectedRow();
        return row < 0 ? null : (String) tableModel.getValueAt(row, 1);
    }

    public String getSelectedUsername() {
        int row = employeeTable.getSelectedRow();
        return row < 0 ? null : (String) tableModel.getValueAt(row, 3);
    }

    public void setEmployees(List<AdminDao.ManagedEmployee> employees) {
        tableModel.setRowCount(0);
        for (AdminDao.ManagedEmployee employee : employees) {
            tableModel.addRow(new Object[]{
                    employee.id(),
                    employee.name(),
                    employee.title(),
                    employee.username(),
                    employee.password()
            });
        }
    }

    public void clearCreateForm() {
        nameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }

    public void clearResetCredentials() {
        resetUsernameField.setText("");
        resetPasswordField.setText("");
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    public void setGeneratingSchedule(boolean generating) {
        generateScheduleButton.setEnabled(!generating);
        if (generating) {
            statusLabel.setText("Generating schedule...");
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Administration Error", JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean confirmRemoval(String employeeName) {
        return JOptionPane.showConfirmDialog(
                this,
                "Remove " + employeeName + " and all of their schedule data?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
}
