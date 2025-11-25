package com.example.online_personal_finance_manager.backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Singleton Pattern for Managing Finance Operations
public class FinanceManager {
    private static FinanceManager instance;
    private DatabaseManager databaseManager;
    private ExecutorService executorService;
    
    // Current session
    private User currentUser;

    private FinanceManager() {
        // Mock Implementation for demonstration purposes (In-Memory Database)
        // This allows the app to work without setting up a MySQL server immediately
        this.databaseManager = new DatabaseManager() {
             private Map<Integer, User> users = new HashMap<>();
             private Map<Integer, List<Transaction>> transactions = new HashMap<>();
             private int nextUserId = 1;

             @Override
             public void addUser(User user) throws Exception {
                 user.setUserId(nextUserId++);
                 users.put(user.getUserId(), user);
                 
                 // Add mock transactions for demo purposes
                 List<Transaction> userTxns = new ArrayList<>();
                 userTxns.add(new Transaction(1, user.getUserId(), 50000.0, TransactionType.INCOME, "Salary", new Date()));
                 userTxns.add(new Transaction(2, user.getUserId(), 20000.0, TransactionType.INCOME, "Freelance", new Date()));
                 userTxns.add(new Transaction(3, user.getUserId(), 12000.0, TransactionType.EXPENSE, "Rent", new Date()));
                 transactions.put(user.getUserId(), userTxns);
                 
                 System.out.println("MockDB: User added: " + user.getUsername() + " ID: " + user.getUserId());
             }

             @Override
             public User getUser(int userId) throws Exception {
                 return users.get(userId);
             }

             @Override
             public User login(String username, String password) throws Exception {
                 for (User u : users.values()) {
                     // Simple check (in real app, hash passwords!)
                     if ((u.getUsername().equals(username) || u.getEmail().equals(username)) 
                             && u.getPassword().equals(password)) {
                         return u;
                     }
                 }
                 return null;
             }

             @Override
             public void addTransaction(Transaction transaction) throws Exception {
                 List<Transaction> list = transactions.get(transaction.getUserId());
                 if (list != null) {
                     list.add(transaction);
                 }
                 System.out.println("MockDB: Transaction added for user " + transaction.getUserId());
             }

             @Override
             public List<Transaction> getTransactions(int userId) throws Exception {
                 List<Transaction> list = transactions.get(userId);
                 return list != null ? new ArrayList<>(list) : new ArrayList<>();
             }
        };
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized FinanceManager getInstance() {
        if (instance == null) {
            instance = new FinanceManager();
        }
        return instance;
    }
    
    public void setDatabaseManager(DatabaseManager dbManager) {
        this.databaseManager = dbManager;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // --- Async Methods for UI ---

    public void register(String fullName, String username, String email, String password, Callback<User> callback) {
        executorService.submit(() -> {
            try {
                User newUser = new User(0, username, email, password);
                // We can store fullName in User class if we update it, for now we just use username
                databaseManager.addUser(newUser);
                this.currentUser = newUser; // Auto-login on register
                callback.onResult(newUser);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void login(String username, String password, Callback<User> callback) {
        executorService.submit(() -> {
            try {
                User user = databaseManager.login(username, password);
                if (user != null) {
                    this.currentUser = user;
                    callback.onResult(user);
                } else {
                    callback.onError(new FinanceException("Invalid credentials"));
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    public void logout() {
        this.currentUser = null;
    }

    public synchronized void addTransaction(Transaction transaction) {
        executorService.submit(() -> {
            try {
                databaseManager.addTransaction(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getTransactions(int userId, Callback<List<Transaction>> callback) {
        executorService.submit(() -> {
            try {
                List<Transaction> transactions = databaseManager.getTransactions(userId);
                callback.onResult(transactions);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
    
    // Helper to calculate totals from a list of transactions
    public FinancialSummary calculateSummary(List<Transaction> transactions) {
        double totalIncome = 0;
        double totalExpense = 0;
        
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome += t.getAmount();
            } else {
                totalExpense += t.getAmount();
            }
        }
        
        double balance = totalIncome - totalExpense;
        double savingsRate = totalIncome > 0 ? ((totalIncome - totalExpense) / totalIncome) * 100 : 0;
        
        return new FinancialSummary(balance, totalIncome, totalExpense, savingsRate);
    }
    
    public static class FinancialSummary {
        public double balance;
        public double income;
        public double expense;
        public double savingsRate;

        public FinancialSummary(double balance, double income, double expense, double savingsRate) {
            this.balance = balance;
            this.income = income;
            this.expense = expense;
            this.savingsRate = savingsRate;
        }
    }

    public interface Callback<T> {
        void onResult(T result);
        void onError(Exception e);
    }
}
