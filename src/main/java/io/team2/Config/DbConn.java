package io.team2.Config;


import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {

    private static final Dotenv dotenv = Dotenv.load();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                dotenv.get("DB_URL"),
                dotenv.get("DB_USER"),
                dotenv.get("DB_PASSWORD")
        );
    }

}
