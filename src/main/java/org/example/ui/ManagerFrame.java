package org.example.ui;
''
import org.example.model.Employee;
import org.example.model.Shift;
import org.example.dao.ShiftDao;
import org.example.model.ShiftTable;

import javax.swing.*;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ManagerFrame extends JFrame
{
    private final Employee currentEmployee;
    private final LocalDate startDate;

    private final ShiftDao shiftDao = new ShiftDao();
    private final ShiftTable shiftTable = new ShiftTable();

    private final JButton refreshButton = new JButton("Refresh");
    private final JLabel statusLabel = new JLabel(" ");

    public ManagerFrame(Employee employee, LocalDate startDate)
    {
        this.currentEmployee = employee;
        this.startDate = startDate;

        setTitle("Shift Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel employeeLabel =
                new JLabel(currentEmployee.getDisplayName() + ": " + startDate + " through " + startDate.plusDays(14));

        JPanel header = new JPanel(new BorderLayout());

        header.add(employeeLabel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(shiftTable), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadSchedule());

        setVisible(true);

        loadSchedule();
    }

    private void loadSchedule()
    {
        refreshButton.setEnabled(false);
        statusLabel.setText("Loading schedule...");

        SwingWorker<List<Shift>, Void> worker =
                new SwingWorker<List<Shift>, Void>()
                {
                    @Override
                    protected List<Shift> doInBackground() throws SQLException
                    {
                        return shiftDao.findForEmployee(
                                currentEmployee.getId(),
                                startDate,
                                startDate.plusDays(14)
                        );
                    }

                    @Override
                    protected void done()
                    {
                        try
                        {
                            List<Shift> shifts = get();

                            shiftTable.setShifts(shifts);

                            statusLabel.setText(
                                    shifts.size() + " shifts found"
                            );
                        }
                        catch (InterruptedException e)
                        {
                            Thread.currentThread().interrupt();
                            statusLabel.setText("Schedule loading interrupted.");
                        }
                        catch (ExecutionException e)
                        {
                            e.getCause().printStackTrace();
                            statusLabel.setText("Could not load schedule.");

                            JOptionPane.showMessageDialog(
                                    ManagerFrame.this,
                                    "Could not load the schedule. Please try again."
                            );
                        }
                        finally
                        {
                            refreshButton.setEnabled(true);
                        }
                    }
                };

        worker.execute();
    }
}