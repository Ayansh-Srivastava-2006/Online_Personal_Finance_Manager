package com.example.online_personal_finance_manager.backend;

import java.util.List;

public interface DatabaseManager {
    void addUser(User user) throws Exception;
    User getUser(int userId) throws Exception;
    User login(String username, String password) throws Exception; // Added login method
    void addTransaction(Transaction transaction) throws Exception;
    List<Transaction> getTransactions(int userId) throws Exception;
}
