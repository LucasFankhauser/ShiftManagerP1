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
    private final JLabel employeeLabel = new JLabel(" ");
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

        header.add(employeeLabel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

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
