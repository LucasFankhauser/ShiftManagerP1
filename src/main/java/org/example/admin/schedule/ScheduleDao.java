package org.example.admin.schedule;

import org.example.Main.Database;
import org.example.model.Employee;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class ScheduleDao {
    public static ArrayList<Employee> get_all_employees(String employee_type) {

        ArrayList<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM employee WHERE employee_title = ?::title;";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee_type);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int employeeId = rs.getInt("employee_id");
                    String employeeName = rs.getString("employee_name");
                    String employeeTitle = rs.getString("employee_title");

                    Employee new_emp = new Employee(
                            employeeId,
                            employeeName,
                            employeeTitle
                    );

                    employees.add(new_emp);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employees", e);
        }

        return employees;
    }

    public static int insertShift(
            Connection connection,
            int employeeId,
            LocalDate date,
            int shiftTypeId,
            boolean requested
    ) throws SQLException {
        String sql = """
                INSERT INTO assigned_shift (
                    employee_id,
                    shift_date,
                    shift_type_id,
                    shift_requested
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            statement.setObject(2, date);
            statement.setInt(3, shiftTypeId);
            statement.setBoolean(4, requested);

            return statement.executeUpdate();
        }
    }

    public static boolean insertGeneratedShift(
            int employeeId,
            LocalDate date,
            int shiftTypeId
    ) {
        try (Connection connection = Database.getConnection()) {
            return insertShift(
                    connection,
                    employeeId,
                    date,
                    shiftTypeId,
                    false
            ) == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void drop_all_shifts() throws SQLException {
        String sql = """
        DELETE FROM assigned_shift
        WHERE shift_requested = false;
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }


}
