package com.example.online_personal_finance_manager.backend;

import java.util.ArrayList;
import java.util.List;

// Placeholder for Firebase implementation
// Real implementation would require adding Firebase SDK dependencies and google-services.json
public class FirebaseDatabaseManager implements DatabaseManager {

    public FirebaseDatabaseManager() {
        // Initialize Firebase Database reference here
    }

    @Override
    public void addUser(User user) throws Exception {
        // Firebase implementation to add user
        // Example: databaseReference.child("users").child(String.valueOf(user.getUserId())).setValue(user);
        System.out.println("Firebase: User added " + user.getUsername());
    }

    @Override
    public User getUser(int userId) throws Exception {
        // Firebase implementation to get user
        return null; 
    }

    @Override
    public User login(String username, String password) throws Exception {
        // Firebase auth implementation
        return null;
    }

    @Override
    public void addTransaction(Transaction transaction) throws Exception {
        // Firebase implementation to add transaction
        System.out.println("Firebase: Transaction added " + transaction.getAmount());
    }

    @Override
    public List<Transaction> getTransactions(int userId) throws Exception {
        // Firebase implementation to get transactions
        return new ArrayList<>();
    }
}
