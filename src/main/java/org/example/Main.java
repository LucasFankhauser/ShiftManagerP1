package org.example;

import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class Main
{
    public static void main(String[] args)
    {
        // Temporary development selection, not actual authentication.
        Employee currentEmployee =
                new Employee(2, "Janice", "doctor");

        // Use the date range that returned your sample assignments.
        LocalDate startDate = LocalDate.of(2026, 9, 21);

        SwingUtilities.invokeLater(() -> {
            new ManagerFrame(currentEmployee, startDate);
        });
    }
}