package org.example.login.NewLogin;

import org.example.Main.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewLoginDao {
    public static boolean create_login(int emp, String username, String password) {

        String sql = """
        INSERT INTO login_data (eid, username, password)
        VALUES (?, ?, ?)
        ON CONFLICT (eid)
        DO UPDATE SET
            username = EXCLUDED.username,
            password = EXCLUDED.password;
        """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, emp);
            statement.setString(2, username);
            statement.setString(3, password);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {

        switch (e.getSQLState()) {
            case "23505":
                throw new IllegalArgumentException("Username is already taken.");

            case "23502":
                throw new IllegalArgumentException("Required fields cannot be empty.");

            case "23514":
                throw new IllegalArgumentException("Username or password does not meet requirements.");

            default:
                throw new RuntimeException("Database error while creating login.", e);
        }
    }
    }
}
