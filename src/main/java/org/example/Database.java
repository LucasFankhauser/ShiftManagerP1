package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database
{
    public static Connection getConnection() throws SQLException
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new SQLException("PostgreSQL JDBC driver was not found.", e);
        }


        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || username == null || password == null)
        {
            throw new SQLException(
                    "Set DB_URL, DB_USER, and DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(url, username, password);
    }
}