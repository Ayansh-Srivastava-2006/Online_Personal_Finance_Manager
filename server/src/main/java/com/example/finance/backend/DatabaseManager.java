package com.example.finance.backend;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    public static Connection getConnection() throws SQLException, FinanceException {
        try {
            return DatabaseConnection.getConnection();
        } catch (ClassNotFoundException e) {
            throw new FinanceException("MySQL JDBC Driver not found.", e);
        }
    }
}

