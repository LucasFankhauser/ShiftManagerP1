package org.example.Main;

import org.example.model.Employee;

import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class Main
{
    public static void main(String[] args)
    {

        // Use the date range that returned your sample assignments.
        LocalDate startDate = LocalDate.of(2026, 9, 21);

        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}