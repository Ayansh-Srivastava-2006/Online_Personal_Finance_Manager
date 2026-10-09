package com.example.finance.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3308/finance_manager"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        String url = getSetting("DB_URL", DEFAULT_URL);
        String user = getSetting("DB_USER", DEFAULT_USER);
        String password = getSetting("DB_PASSWORD", null);
        if (password == null || password.isEmpty()) {
            password = getSetting("DB_PASS", null);
        }
        if (password == null || password.isEmpty()) {
            throw new SQLException(
                    "Database password is not configured. Set DB_PASSWORD (or DB_PASS) "
                            + "in the environment or as a system property.");
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private static String getSetting(String name, String defaultValue) {
        String systemProperty = System.getProperty(name);
        if (systemProperty != null && !systemProperty.isEmpty()) {
            return systemProperty;
        }

        String environmentValue = System.getenv(name);
        return environmentValue != null && !environmentValue.isEmpty()
                ? environmentValue
                : defaultValue;
    }
}
