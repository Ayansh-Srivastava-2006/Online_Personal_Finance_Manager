package com.example.finance.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseHelper {
    // DB credentials from environment variables or use defaults
    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/finance_db";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "password";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public User login(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (BCrypt.checkpw(password, storedHash)) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public void register(String username, String email, String password) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
        }
    }

    public List<Transaction> getTransactions(int userId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDate("date")
                ));
            }
        }
        return list;
    }

    public void addTransaction(Transaction t) throws SQLException {
        String query = "INSERT INTO transactions (user_id, amount, type, category, date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, t.getUserId());
            stmt.setDouble(2, t.getAmount());
            stmt.setString(3, t.getType());
            stmt.setString(4, t.getCategory());
            stmt.setDate(5, new java.sql.Date(t.getDate().getTime()));
            stmt.executeUpdate();
        }
    }
    
    // Inner classes for Data Transfer Objects
    public static class User {
        int id; String username; String email;
        public User(int id, String u, String e) { this.id=id; this.username=u; this.email=e; }
    }
    
    public static class Transaction {
        int id; int userId; double amount; String type; String category; Date date;
        public Transaction(int id, int uid, double amt, String type, String cat, Date date) {
            this.id=id; this.userId=uid; this.amount=amt; this.type=type; this.category=cat; this.date=date;
        }
        public int getUserId() { return userId; }
        public double getAmount() { return amount; }
        public String getType() { return type; }
        public String getCategory() { return category; }
        public Date getDate() { return date; }
    }
}
