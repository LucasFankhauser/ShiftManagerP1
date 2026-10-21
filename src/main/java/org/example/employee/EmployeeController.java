package org.example.employee;

import org.example.Main.MainFrame;
import org.example.model.Employee;
import org.example.model.Shift;

import javax.swing.SwingWorker;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EmployeeController
{
    private final MainFrame mainFrame;
    private final ShiftDao shiftDao = new ShiftDao();

    private EmployeePanel panel;
    private Employee currentEmployee;
    private LocalDate startDate;

    private boolean loading;

    public EmployeeController(MainFrame mainFrame)
    {
        this.mainFrame = mainFrame;
    }

    // Call after the employee has logged in and the panel exists.
    public void initialize()
    {
        panel = mainFrame.getEmployeePanel();
        currentEmployee = mainFrame.getCurrentEmployee();
        startDate = LocalDate.now();

        panel.setEmployeeDetails(currentEmployee, startDate);

        loadSchedule();
    }

    public void loadSchedule()
    {
        if (loading)
        {
            return;
        }

        loading = true;
        panel.setLoading(true);

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
                            panel.setShifts(shifts);
                            panel.setStatus(shifts.size() + " shifts found");
                        }
                        catch (InterruptedException e)
                        {
                            Thread.currentThread().interrupt();
                            panel.setStatus("Schedule loading interrupted.");
                        }
                        catch (ExecutionException e)
                        {
                            e.getCause().printStackTrace();
                            panel.setStatus("Could not load schedule.");
                            panel.showError(
                                    "Could not load the schedule. Please try again."
                            );
                        }
                        finally
                        {
                            loading = false;
                            panel.setLoading(false);
                        }
                    }
                };

        worker.execute();
    }
}