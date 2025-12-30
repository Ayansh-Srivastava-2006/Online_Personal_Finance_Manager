package com.example.finance.backend;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FinanceDAO {

    public void addUser(User user, String password) throws FinanceException {
        String sql = "INSERT INTO users(uid, fullName, username, email, password_hash, salt) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            byte[] salt = PasswordUtils.getSalt();
            byte[] hashedPassword = PasswordUtils.getHashedPassword(password, salt);

            pstmt.setString(1, user.getUid());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getEmail());
            pstmt.setBytes(5, hashedPassword);
            pstmt.setBytes(6, salt);

            pstmt.executeUpdate();

        } catch (SQLException | NoSuchAlgorithmException | ClassNotFoundException e) {
            throw new FinanceException("Error adding user to the database: " + e.getMessage(), e);
        }
    }

    public User login(String email, String password) throws FinanceException {
        String sql = "SELECT uid, fullName, username, email, password_hash, salt FROM users WHERE email = ?";
        User user = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] storedHash = rs.getBytes("password_hash");
                    byte[] storedSalt = rs.getBytes("salt");

                    if (PasswordUtils.verifyPassword(password, storedHash, storedSalt)) {
                        String uid = rs.getString("uid");
                        String fullName = rs.getString("fullName");
                        String username = rs.getString("username");
                        user = new User(uid, fullName, username, email, null);
                    }
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error during login: " + e.getMessage(), e);
        }
        return user;
    }

    public List<Account> getAccounts() throws FinanceException {
        String sql = "SELECT accountId, name, type, balance FROM accounts";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String accountId = rs.getString("accountId");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double balance = rs.getDouble("balance");

                accounts.add(new Account(accountId, name, type, balance));
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error retrieving accounts from the database: " + e.getMessage(), e);
        }

        return accounts;
    }

    public void addAccount(Account account) throws FinanceException {
        String sql = "INSERT INTO accounts(accountId, name, type, balance) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountId());
            pstmt.setString(2, account.getName());
            pstmt.setString(3, account.getType());
            pstmt.setDouble(4, account.getBalance());

            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error adding account to the database: " + e.getMessage(), e);
        }
    }

    public void updateAccount(Account account) throws FinanceException {
        String sql = "UPDATE accounts SET name = ?, type = ?, balance = ? WHERE accountId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getName());
            pstmt.setString(2, account.getType());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setString(4, account.getAccountId());

            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error updating account in the database: " + e.getMessage(), e);
        }
    }

    public void deleteAccount(String accountId) throws FinanceException {
        String sql = "DELETE FROM accounts WHERE accountId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountId);
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error deleting account from the database: " + e.getMessage(), e);
        }
    }

    public List<Budget> getBudgets() throws FinanceException {
        String sql = "SELECT budgetId, category, amount, spent FROM budgets";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String budgetId = rs.getString("budgetId");
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                double spent = rs.getDouble("spent");

                budgets.add(new Budget(budgetId, category, amount, spent));
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error retrieving budgets from the database: " + e.getMessage(), e);
        }

        return budgets;
    }

    public void addBudget(Budget budget) throws FinanceException {
        String sql = "INSERT INTO budgets(budgetId, category, amount, spent) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, budget.getBudgetId());
            pstmt.setString(2, budget.getCategory());
            pstmt.setDouble(3, budget.getAmount());
            pstmt.setDouble(4, budget.getSpent());

            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error adding budget to the database: " + e.getMessage(), e);
        }
    }

    public void updateBudget(Budget budget) throws FinanceException {
        String sql = "UPDATE budgets SET category = ?, amount = ?, spent = ? WHERE budgetId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, budget.getCategory());
            pstmt.setDouble(2, budget.getAmount());
            pstmt.setDouble(3, budget.getSpent());
            pstmt.setString(4, budget.getBudgetId());

            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error updating budget in the database: " + e.getMessage(), e);
        }
    }

    public void deleteBudget(String budgetId) throws FinanceException {
        String sql = "DELETE FROM budgets WHERE budgetId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, budgetId);
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error deleting budget from the database: " + e.getMessage(), e);
        }
    }

    public List<Transaction> getTransactions() throws FinanceException {
        String sql = "SELECT transactionId, accountId, amount, type, category, date FROM transactions";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String transactionId = rs.getString("transactionId");
                String accountId = rs.getString("accountId");
                double amount = rs.getDouble("amount");
                TransactionType type = TransactionType.valueOf(rs.getString("type"));
                String category = rs.getString("category");
                long dateMillis = rs.getLong("date");
                
                transactions.add(new Transaction(transactionId, accountId, amount, type, category, dateMillis));
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error retrieving transactions from the database: " + e.getMessage(), e);
        }

        return transactions;
    }

    public void addTransaction(Transaction transaction) throws FinanceException {
        String sql = "INSERT INTO transactions(transactionId, accountId, amount, type, category, date) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getAccountId());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getType().name());
            pstmt.setString(5, transaction.getCategory());
            pstmt.setLong(6, transaction.getDate());

            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new FinanceException("Error adding transaction to the database: " + e.getMessage(), e);
        }
    }
}
