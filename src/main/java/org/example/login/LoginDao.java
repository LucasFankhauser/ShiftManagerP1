package org.example.login;

import org.example.Main.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {

    public static int validate_login(String username, String password) {

        String sql = "SELECT login_eid FROM validate_login(?, ?);";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int employeeId = rs.getInt("login_eid");
                    System.out.println(employeeId);
                    if (!rs.wasNull()) {
                        return employeeId;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error executing login validation function", e);
        }

        return -1;
    }

    public static Object[] get_emp(int eid) {

        String sql = "SELECT * FROM get_emp(?);";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eid);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    Object[] data = new Object[3];

                    data[0] = rs.getInt(1);
                    data[1] = rs.getString(2);
                    data[2] = rs.getString(3);

                    return data;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee", e);
        }

        return null;
    }
}