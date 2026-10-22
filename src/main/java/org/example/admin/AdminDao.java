package org.example.admin;

import org.example.Main.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {
    public record ManagedEmployee(
            int id,
            String name,
            String title,
            String username,
            String password
    ) {}

    public List<ManagedEmployee> findEmployees() throws SQLException {
        String sql = """
                SELECT e.employee_id, e.employee_name, e.employee_title,
                       l.username, l.password
                FROM employee AS e
                LEFT JOIN login_data AS l ON l.eid = e.employee_id
                WHERE e.employee_title IN ('doctor', 'technician')
                ORDER BY e.employee_name ASC
                """;

        List<ManagedEmployee> employees = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                employees.add(new ManagedEmployee(
                        results.getInt("employee_id"),
                        results.getString("employee_name"),
                        results.getString("employee_title"),
                        results.getString("username"),
                        results.getString("password")
                ));
            }
        }

        return employees;
    }

    public void createEmployee(
            String name,
            String title,
            String username,
            String password
    ) throws SQLException {
        String employeeSql = """
                INSERT INTO employee (employee_name, employee_title)
                VALUES (?, ?::title)
                RETURNING employee_id
                """;
        String loginSql = """
                INSERT INTO login_data (eid, username, password)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int employeeId;

                try (PreparedStatement statement =
                             connection.prepareStatement(employeeSql)) {
                    statement.setString(1, name);
                    statement.setString(2, title);

                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new SQLException(
                                    "Employee insert did not return an id."
                            );
                        }
                        employeeId = result.getInt("employee_id");
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(loginSql)) {
                    statement.setInt(1, employeeId);
                    statement.setString(2, username);
                    statement.setString(3, password);
                    statement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean updateCredentials(
            int employeeId,
            String username,
            String password
    )
            throws SQLException {
        String sql = """
                UPDATE login_data
                SET username = ?, password = ?
                WHERE eid = ?
                  AND EXISTS (
                      SELECT 1
                      FROM employee
                      WHERE employee_id = ?
                        AND employee_title IN ('doctor', 'technician')
                  )
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setInt(3, employeeId);
            statement.setInt(4, employeeId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean removeEmployee(int employeeId) throws SQLException {
        String checkSql = """
                SELECT 1
                FROM employee
                WHERE employee_id = ?
                  AND employee_title IN ('doctor', 'technician')
                """;
        String[] deleteSql = {
                "DELETE FROM assigned_shift WHERE employee_id = ?",
                "DELETE FROM employee_time_off WHERE employee_id = ?",
                "DELETE FROM login_data WHERE eid = ?",
                "DELETE FROM employee WHERE employee_id = ?"
        };

        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(checkSql)) {
                    statement.setInt(1, employeeId);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                int employeeRows = 0;
                for (String sql : deleteSql) {
                    try (PreparedStatement statement =
                                 connection.prepareStatement(sql)) {
                        statement.setInt(1, employeeId);
                        employeeRows = statement.executeUpdate();
                    }
                }

                connection.commit();
                return employeeRows == 1;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
