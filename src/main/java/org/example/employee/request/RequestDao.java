package org.example.employee.request;

import org.example.Main.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.model.RequestDecision;
import org.example.model.TimeOffEntry;
import org.example.model.TimeOffType;

public class RequestDao {
    public List<TimeOffEntry> findTimeOffForEmployee(
            int employeeId
    ) throws SQLException
    {
        String sql = """
                SELECT employee_id,
                date,
                type::text AS time_off_type
                FROM employee_time_off
                WHERE employee_id = ?
                ORDER BY date DESC
                """;

        List<TimeOffEntry> entries = new ArrayList<>();


        try (Connection connection = Database.getConnection();
        PreparedStatement statement =
                connection.prepareStatement(sql))
        {
            statement.setInt(1, employeeId);
            try (ResultSet results = statement.executeQuery())
            {
                while(results.next())
                {
                    int resultEmployeeId =
                            results.getInt("employee_id");

                    LocalDate date =
                            results.getObject("date", LocalDate.class);

                    TimeOffType type =
                            TimeOffType.fromDatabase(
                                    results.getString("time_off_type")
                            );
                    TimeOffEntry entry = new TimeOffEntry(
                            resultEmployeeId,
                            date,
                            type
                    );

                    entries.add(entry);
                }
            }
        }

        return entries;
    }


    public RequestDecision requestTimeOff(int employeeId, LocalDate date, TimeOffType type) throws SQLException
        {
            String insertSql = """
                    INSERT INTO employee_time_off (employee_id, date, type)
                    VALUES (?, ?, ?::time_off)
                    """;

            String removeShiftsSql = """
            DELETE FROM assigned_shift
            WHERE employee_id = ?
              AND shift_date = ?
            """;

            try (Connection connection = Database.getConnection())
            {
                connection.setAutoCommit(false);
            try
            {
                try (PreparedStatement statement =
                             connection.prepareStatement(insertSql))
                {
                    statement.setInt(1, employeeId);
                    statement.setObject(2, date);
                    statement.setString(
                            3,
                            type.getDatabaseValue()
                    );

                    statement.executeUpdate();
                }

                if (type == TimeOffType.SICK)
                {
                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         removeShiftsSql
                                 ))
                    {
                        statement.setInt(1, employeeId);
                        statement.setObject(2, date);
                        statement.executeUpdate();
                    }
                }

                connection.commit();

                return RequestDecision.approved(
                        "Time-off request approved."
                );
            }
            catch (SQLException exception)
            {
                connection.rollback();

                if (isRuleDenial(exception))
                {
                    return RequestDecision.denied(
                            getDatabaseMessage(exception)
                    );
                }

                throw exception;
            }
        }
    }

    private boolean isRuleDenial(SQLException exception)
    {
        String sqlState = exception.getSQLState();

        return "P0001".equals(sqlState)
                || (sqlState != null && sqlState.startsWith("23"));
    }

    private String getDatabaseMessage(SQLException exception)
    {
        String message = exception.getMessage();

        if (message == null || message.isBlank())
        {
            return "The request was denied.";
        }

        return message;
    }

}
