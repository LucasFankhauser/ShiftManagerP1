package org.example.employee;

import org.example.Main.Database;
import org.example.model.Shift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ShiftDao
{
    public List<Shift> findForEmployee(
            int employeeId,
            LocalDate startDate,
            LocalDate endDate) throws SQLException
    {
        String sql = """
                SELECT a.shift_date,
                       st.shift_name,
                       st.shift_start_time,
                       st.shift_end_time
                FROM assigned_shift AS a
                JOIN shift_type AS st
                  ON a.shift_type_id = st.shift_type_id
                WHERE a.employee_id = ?
                  AND a.shift_date >= ?
                  AND a.shift_date < ?
                ORDER BY a.shift_date, st.shift_start_time, a.shift_id
                """;

        List<Shift> shifts = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql))
        {
            statement.setInt(1, employeeId);
            statement.setObject(2, startDate);
            statement.setObject(3, endDate);

            try (ResultSet results = statement.executeQuery())
            {
                while (results.next())
                {
                    Shift shift = new Shift(
                            results.getObject(
                                    "shift_date", LocalDate.class),
                            results.getString("shift_name"),
                            results.getObject(
                                    "shift_start_time", LocalTime.class),
                            results.getObject(
                                    "shift_end_time", LocalTime.class)
                    );

                    shifts.add(shift);
                }
            }
        }

        return shifts;
    }
}