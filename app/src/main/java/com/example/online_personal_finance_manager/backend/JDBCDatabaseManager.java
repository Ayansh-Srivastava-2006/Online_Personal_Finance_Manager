package com.example.online_personal_finance_manager.backend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCDatabaseManager implements DatabaseManager {

    @Override
    public User login(String email, String password) throws Exception { return null; }

    @Override
    public void addUser(User user) throws Exception { }

    @Override
    public void addTransaction(Transaction transaction) throws Exception { }

    @Override
    public List<Transaction> getTransactions(int userId) throws Exception { return new ArrayList<>(); }

    @Override
    public void addAccount(Account account) throws Exception { }

    @Override
    public List<Account> getAccounts(int userId) throws Exception { return new ArrayList<>(); }

    @Override
    public void deleteAccount(String accountId) throws Exception { }

    @Override
    public void updateAccount(Account account) throws Exception { }

    @Override
    public void addBudget(Budget budget) throws Exception { }

    @Override
    public List<Budget> getBudgets(int userId) throws Exception { return new ArrayList<>(); }

    @Override
    public void updateBudget(Budget budget) throws Exception { }

    @Override
    public void deleteBudget(String budgetId) throws Exception { }

    @Override
    public User getUser(int userId) throws Exception { return null; }
}
