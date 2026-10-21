package org.example.employee;

import org.example.model.Employee;
import org.example.model.Shift;

import javax.swing.SwingWorker;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Handles employee schedule actions. Construct and call it on Swing's EDT. */
public class EmployeeController
{
    private final EmployeePanel panel;
    private final Employee currentEmployee;
    private final LocalDate startDate;
    private final ShiftDao shiftDao = new ShiftDao();

    // Accessed only on the EDT; prevents overlapping schedule loads.
    private boolean loading;

    public EmployeeController(
            EmployeePanel panel,
            Employee employee,
            LocalDate startDate)
    {
        this.panel = panel;
        this.currentEmployee = employee;
        this.startDate = startDate;

        panel.setEmployeeDetails(employee, startDate);
        panel.addRefreshListener(e -> loadSchedule());
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
