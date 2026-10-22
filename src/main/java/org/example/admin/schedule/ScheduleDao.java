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

    public static boolean insert_shift(int emp, LocalDate date, int shift_type) {

        String sql = """
        INSERT INTO assigned_shift (employee_id, shift_date, shift_type_id)
        VALUES (?,?,?);
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, emp);
            statement.setDate(2, Date.valueOf(date));
            statement.setInt(3, shift_type);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public static void drop_all_shifts() {
        String sql = """
        TRUNCATE TABLE assigned_shift;
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("failed to clear assigned shifts");
        }
    }


}
