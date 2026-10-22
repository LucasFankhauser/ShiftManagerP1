package org.example.employee;

import org.example.model.Employee;
import org.example.model.Shift;
import org.example.model.ShiftTable;
import org.example.employee.request.RequestPane;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/** Displays employee schedule information. Database work belongs in the controller. */
public class EmployeePanel extends JPanel
{
    private final ShiftTable shiftTable = new ShiftTable();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton logoutButton = new JButton("Log out");
    private final JLabel employeeLabel = new JLabel(" ");
    private final JLabel vacationDaysLabel = new JLabel("Vacation Days: 0/8");
    private final JLabel sickDaysLabel = new JLabel("Sick Days: 0/4");
    private final JLabel statusLabel = new JLabel(" ");

    public EmployeePanel(EmployeeController employeeController, RequestPane requestPane)
    {
        setLayout(new BorderLayout());

        JPanel schedulePanel = new JPanel(
                new BorderLayout()
        );

        JPanel header = new JPanel(
                new BorderLayout()
        );

        JPanel headerButtons = new JPanel();
        headerButtons.add(refreshButton);
        headerButtons.add(logoutButton);

        JPanel employeeDetails = new JPanel();
        employeeDetails.setLayout(
                new BoxLayout(employeeDetails, BoxLayout.Y_AXIS)
        );
        employeeDetails.add(employeeLabel);
        employeeDetails.add(vacationDaysLabel);
        employeeDetails.add(sickDaysLabel);

        header.add(employeeDetails, BorderLayout.WEST);
        header.add(headerButtons, BorderLayout.EAST);

        schedulePanel.add(header, BorderLayout.NORTH);
        schedulePanel.add(
                new JScrollPane(shiftTable),
                BorderLayout.CENTER
        );
        schedulePanel.add(
                statusLabel,
                BorderLayout.SOUTH
        );

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Schedule", schedulePanel);
        tabs.addTab("Requests", requestPane);

        add(tabs, BorderLayout.CENTER);

        addRefreshListener(
                event -> employeeController.loadSchedule()
        );
        logoutButton.addActionListener(
                event -> employeeController.logout()
        );
    }

    public void setEmployeeDetails(Employee employee, LocalDate startDate)
    {
        // The query excludes startDate + 14, so the last included date is + 13.
        employeeLabel.setText(
                employee.getDisplayName() + ": " + startDate
                        + " through " + startDate.plusDays(13)
        );
    }

    public void addRefreshListener(ActionListener listener)
    {
        refreshButton.addActionListener(listener);
    }

    public void setShifts(List<Shift> shifts)
    {
        shiftTable.setShifts(shifts);
    }

    public void setTimeOffUsage(int vacationUsed, int sickUsed)
    {
        vacationDaysLabel.setText("Vacation Days: " + vacationUsed + "/8");
        sickDaysLabel.setText("Sick Days: " + sickUsed + "/4");
    }

    public void setLoading(boolean loading)
    {
        refreshButton.setEnabled(!loading);

        if (loading)
        {
            statusLabel.setText("Loading schedule...");
        }
    }

    public void setStatus(String message)
    {
        statusLabel.setText(message);
    }

    public void showError(String message)
    {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Schedule Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
