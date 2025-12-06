package com.example.online_personal_finance_manager.backend;

import java.util.List;

public interface DatabaseManager {
    User login(String email, String password) throws Exception;
    void addUser(User user) throws Exception;

    void addTransaction(Transaction transaction) throws Exception;
    List<Transaction> getTransactions(int userId) throws Exception; 

    void addAccount(Account account) throws Exception;
    List<Account> getAccounts(int userId) throws Exception;
    void deleteAccount(String accountId) throws Exception;

    void updateAccount(Account account) throws Exception;
    
    void addBudget(Budget budget) throws Exception;
    List<Budget> getBudgets(int userId) throws Exception;
    void updateBudget(Budget budget) throws Exception;
    void deleteBudget(String budgetId) throws Exception; // Changed to String

    User getUser(int userId) throws Exception;
}
