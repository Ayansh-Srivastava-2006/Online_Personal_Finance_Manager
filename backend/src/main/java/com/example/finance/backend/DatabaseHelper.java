package com.example.finance.backend;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DatabaseHelper {
    // Database credentials from environment variables
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

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
                if (BCrypt.checkpw(password, rs.getString("password"))) {
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
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
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

    public String createSession(int userId) throws SQLException {
        String sessionId = UUID.randomUUID().toString();
        // Session expires in 24 hours
        long expiryTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        String query = "INSERT INTO sessions (session_id, user_id, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            stmt.setInt(2, userId);
            stmt.setTimestamp(3, new Timestamp(expiryTime));
            stmt.executeUpdate();
            return sessionId;
        }
    }

    public User getUserFromSession(String sessionId) throws SQLException {
        String query = "SELECT u.* FROM users u JOIN sessions s ON u.user_id = s.user_id " +
                "WHERE s.session_id = ? AND s.expires_at > NOW()";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email")
                );
            }
        }
        return null;
    }

    public void deleteSession(String sessionId) throws SQLException {
        String query = "DELETE FROM sessions WHERE session_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        }
    }
    
    // Inner classes for Data Transfer Objects
    public static class User {
        int id; String username; String email;
        public User(int id, String u, String e) { this.id=id; this.username=u; this.email=e; }
        public int getId() { return id; }
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
