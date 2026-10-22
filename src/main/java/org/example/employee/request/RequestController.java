package org.example.employee.request;

import org.example.Main.MainFrame;
import org.example.employee.EmployeeController;
import org.example.model.Employee;
import org.example.model.RequestDecision;
import org.example.model.RequestKind;
import org.example.model.ShiftChoice;
import org.example.model.TimeOffType;

import javax.swing.SwingWorker;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

public class RequestController
{
    private final MainFrame mainFrame;
    private final RequestPane pane;
    private final EmployeeController employeeController;
    private final RequestDao requestDao =
            new RequestDao();

    private boolean submitting;

    public RequestController(
            MainFrame mainFrame,
            RequestPane pane,
            EmployeeController employeeController
    )
    {
        this.mainFrame = mainFrame;
        this.pane = pane;
        this.employeeController = employeeController;

        pane.addSubmitListener(
                event -> submitRequest()
        );
    }

    private void submitRequest()
    {
        if (submitting)
        {
            return;
        }

        Employee employee = mainFrame.getCurrentEmployee();

        if (employee == null)
        {
            pane.showError(
                    "No employee is currently logged in."
            );
            return;
        }


//       Read the Swing component values now, while this
//       method is running on Swing's event-dispatch thread.

        RequestKind kind = pane.getSelectedRequestKind();

        LocalDate date = pane.getSelectedDate();

        TimeOffType timeOffType = pane.getSelectedTimeOffType();

        ShiftChoice shift = pane.getSelectedShift();

        if (kind == RequestKind.WORK && shift == null)
        {
            pane.showError(
                    "Select a shift before submitting."
            );
            return;
        }

        submitting = true;
        pane.setSubmitting(true);

        SwingWorker<RequestDecision, Void> worker =
                new SwingWorker<>()
                {
                    @Override
                    protected RequestDecision doInBackground()
                            throws SQLException
                    {
                        if (kind == RequestKind.TIME_OFF)
                        {
                            return requestDao.requestTimeOff(
                                    employee.getId(),
                                    date,
                                    timeOffType
                            );
                        }

                        return requestDao.requestWork(
                                employee.getId(),
                                date,
                                shift.id()
                        );
                    }

                    @Override
                    protected void done()
                    {
                        try
                        {
                            RequestDecision decision = get();

                            pane.showDecision(decision);

                            if (decision.approved())
                            {
                                employeeController
                                        .loadSchedule();
                            }
                        }
                        catch (InterruptedException exception)
                        {
                            Thread.currentThread().interrupt();

                            pane.showError(
                                    "Request submission was interrupted."
                            );
                        }
                        catch (ExecutionException exception)
                        {
                            exception.getCause()
                                    .printStackTrace();

                            pane.showError(
                                    "Could not submit the request. "
                                            + "Check the database connection."
                            );
                        }
                        finally
                        {
                            submitting = false;
                            pane.setSubmitting(false);
                        }
                    }
                };

        worker.execute();
    }
}