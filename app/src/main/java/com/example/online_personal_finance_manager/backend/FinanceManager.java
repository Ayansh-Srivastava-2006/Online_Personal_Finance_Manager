package com.example.online_personal_finance_manager.backend;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceManager {
    private static FinanceManager instance;
    private final DatabaseManager databaseManager;
    private final ExecutorService executorService;
    private User currentUser;

    private FinanceManager() {
        this.executorService = Executors.newFixedThreadPool(4);
        this.databaseManager = new MockDatabaseManager();
    }

    public static synchronized FinanceManager getInstance() {
        if (instance == null) {
            instance = new FinanceManager();
        }
        return instance;
    }
    
    public void initialize(Context context) {
        if (databaseManager instanceof MockDatabaseManager) {
            ((MockDatabaseManager) databaseManager).loadFromAssets(context);
        }
    }

    public User getCurrentUser() { return currentUser; }
    public void logout() { this.currentUser = null; }

    // --- Async Wrappers for UI ---
    public void register(String email, String password, Callback<User> c) { 
        executorService.submit(() -> { try { User n = new User(null, email, password); databaseManager.addUser(n); this.currentUser = databaseManager.login(email, password); c.onResult(this.currentUser); } catch (Exception e) { c.onError(e); } }); 
    }
    public void login(String email, String password, Callback<User> c) { 
        executorService.submit(() -> { try { User u = databaseManager.login(email, password); if(u!=null) { this.currentUser=u; c.onResult(u); } else c.onError(new FinanceException("Invalid")); } catch (Exception e) { c.onError(e); } }); 
    }
    public void addTransaction(Transaction t, Callback<Boolean> c) { executorService.submit(() -> { try { databaseManager.addTransaction(t); if(c!=null)c.onResult(true); } catch (Exception e) { if(c!=null)c.onError(e); } }); }
    public void getTransactions(Callback<List<Transaction>> c) { executorService.submit(() -> { try { c.onResult(databaseManager.getTransactions(0)); } catch (Exception e) { c.onError(e); } }); }
    public void addAccount(Account a, Callback<Boolean> c) { executorService.submit(() -> { try { databaseManager.addAccount(a); if(c!=null)c.onResult(true); } catch (Exception e) { if(c!=null)c.onError(e); } }); }
    public void getAccounts(Callback<List<Account>> c) { executorService.submit(() -> { try { c.onResult(databaseManager.getAccounts(0)); } catch (Exception e) { c.onError(e); } }); }
    public void deleteAccount(String id, Callback<Boolean> c) { executorService.submit(() -> { try { databaseManager.deleteAccount(id); if(c!=null)c.onResult(true); } catch (Exception e) { if(c!=null)c.onError(e); } }); }
    public void addBudget(Budget b, Callback<Boolean> c) { executorService.submit(() -> { try { databaseManager.addBudget(b); if(c!=null)c.onResult(true); } catch (Exception e) { if(c!=null)c.onError(e); } }); }
    public void deleteBudget(String id, Callback<Boolean> c) { executorService.submit(() -> { try { databaseManager.deleteBudget(id); if(c!=null)c.onResult(true); } catch (Exception e) { if(c!=null)c.onError(e); } }); }
    public void getBudgets(Callback<List<Budget>> c) { executorService.submit(() -> { try { c.onResult(databaseManager.getBudgets(0)); } catch (Exception e) { c.onError(e); } }); }

    public FinancialSummary calculateSummary(List<Transaction> transactions) {
        double income = 0, expense = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) income += t.getAmount(); else expense += t.getAmount();
        }
        return new FinancialSummary(income - expense, income, expense, income > 0 ? ((income - expense) / income) * 100 : 0);
    }

    public static class FinancialSummary {
        public double balance, income, expense, savingsRate;
        public FinancialSummary(double b, double i, double e, double s) { this.balance = b; this.income = i; this.expense = e; this.savingsRate = s; }
    }
    public interface Callback<T> { void onResult(T result); void onError(Exception e); }
    
    // --- Mock DB Implementation ---
    class MockDatabaseManager implements DatabaseManager {
             private Map<String, User> users = new HashMap<>();
             private Map<String, List<Transaction>> transactions = new HashMap<>();
             private Map<String, List<Account>> accounts = new HashMap<>();
             private Map<String, List<Budget>> budgets = new HashMap<>();
             private int nextIdCounter = 1;
             
             private String generateId() { return String.valueOf(nextIdCounter++); }
             
             public void loadFromAssets(Context context) {
                 try {
                     Gson gson = new Gson();
                     // Define structure matching the JSON export
                     Type type = new TypeToken<Map<String, Map<String, UserData>>>(){}.getType();
                     // The root of export is usually "users" -> {uid: UserData} or just the root Map
                     // Let's assume the file contains the root object: {"users": ...}
                     Map<String, Object> root = gson.fromJson(new InputStreamReader(context.getAssets().open("initial_data.json")), new TypeToken<Map<String, Object>>(){}.getType());
                     
                     if (root != null && root.containsKey("users")) {
                         String usersJson = gson.toJson(root.get("users"));
                         Map<String, UserData> loadedUsers = gson.fromJson(usersJson, new TypeToken<Map<String, UserData>>(){}.getType());
                         
                         if (loadedUsers != null) {
                             for (Map.Entry<String, UserData> entry : loadedUsers.entrySet()) {
                                 String uid = entry.getKey();
                                 UserData data = entry.getValue();
                                 
                                 // Reconstruct User object
                                 User u = new User(uid, data.email, data.password != null ? data.password : "123456");
                                 u.username = data.username;
                                 users.put(uid, u);
                                 
                                 // Load Transactions
                                 if (data.transactions != null) {
                                     List<Transaction> txns = new ArrayList<>(data.transactions.values());
                                     // Ensure IDs are set
                                     for(Map.Entry<String, Transaction> te : data.transactions.entrySet()) {
                                         te.getValue().transactionId = te.getKey();
                                         // Fix missing date if any
                                         if(te.getValue().date == 0) te.getValue().date = new Date().getTime();
                                     }
                                     transactions.put(uid, txns);
                                 }
                                 
                                 // Load Accounts
                                 if (data.accounts != null) {
                                     List<Account> accs = new ArrayList<>(data.accounts.values());
                                     for(Map.Entry<String, Account> ae : data.accounts.entrySet()) ae.getValue().accountId = ae.getKey();
                                     accounts.put(uid, accs);
                                 }
                                 
                                 // Load Budgets
                                 if (data.budgets != null) {
                                     List<Budget> buds = new ArrayList<>(data.budgets.values());
                                     for(Map.Entry<String, Budget> be : data.budgets.entrySet()) be.getValue().budgetId = be.getKey();
                                     budgets.put(uid, buds);
                                 }
                             }
                             System.out.println("MockDB: Loaded " + loadedUsers.size() + " users from JSON.");
                         }
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                     System.out.println("MockDB: Could not load initial_data.json: " + e.getMessage());
                     // Fallback to hardcoded data is handled by addUser if empty, or just rely on manual add
                 }
             }
             
             // Helper class for JSON mapping
             class UserData {
                 String username, email, password;
                 Map<String, Transaction> transactions;
                 Map<String, Account> accounts;
                 Map<String, Budget> budgets;
             }

             @Override
             public void addUser(User user) throws Exception {
                 user.uid = generateId();
                 users.put(user.uid, user);
                 // Add default mock data only if we didn't load from file (or if file empty)
                 if (users.size() <= 1) { 
                     addHardcodedMockData(user.uid);
                 }
             }
             
             private void addHardcodedMockData(String uid) {
                 List<Account> userAccounts = new ArrayList<>();
                 userAccounts.add(new Account(generateId(), "Primary Savings", "Savings", 50000.0));
                 accounts.put(uid, userAccounts);
                 
                 List<Transaction> userTxns = new ArrayList<>();
                 Calendar cal = Calendar.getInstance();
                 cal.add(Calendar.MONTH, -2); 
                 userTxns.add(new Transaction(generateId(), 48000.0, TransactionType.INCOME, "Salary", cal.getTime()));
                 userTxns.add(new Transaction(generateId(), 3000.0, TransactionType.EXPENSE, "Groceries & Food", cal.getTime()));
                 // ... (rest of hardcoded data) ...
                 transactions.put(uid, userTxns);
                 
                 List<Budget> userBudgets = new ArrayList<>();
                 userBudgets.add(new Budget(generateId(), "Groceries & Food", 8000.0, 3500.0));
                 budgets.put(uid, userBudgets);
             }

             @Override
             public User login(String email, String password) throws Exception {
                 for (User u : users.values()) {
                     if (u.getEmail().equals(email) && u.getPassword().equals(password)) return u;
                 }
                 return null;
             }

             @Override public void addTransaction(Transaction t) throws Exception { if (currentUser == null) return; transactions.computeIfAbsent(currentUser.getUid(), k -> new ArrayList<>()).add(t); }
             @Override public List<Transaction> getTransactions(int userId) throws Exception { if (currentUser == null) return new ArrayList<>(); return new ArrayList<>(transactions.getOrDefault(currentUser.getUid(), new ArrayList<>())); }
             @Override public void addAccount(Account a) throws Exception { if (currentUser == null) return; a.accountId = generateId(); accounts.computeIfAbsent(currentUser.getUid(), k -> new ArrayList<>()).add(a); }
             @Override public List<Account> getAccounts(int userId) throws Exception { if (currentUser == null) return new ArrayList<>(); return new ArrayList<>(accounts.getOrDefault(currentUser.getUid(), new ArrayList<>())); }
             @Override public void deleteAccount(String accountId) throws Exception { if (currentUser == null) return; List<Account> list = accounts.get(currentUser.getUid()); if (list != null) list.removeIf(a -> a.getAccountId().equals(accountId)); }
             @Override public void updateAccount(Account account) throws Exception { }
             @Override public void addBudget(Budget b) throws Exception { if (currentUser == null) return; b.budgetId = generateId(); budgets.computeIfAbsent(currentUser.getUid(), k -> new ArrayList<>()).add(b); }
             @Override public List<Budget> getBudgets(int userId) throws Exception { if (currentUser == null) return new ArrayList<>(); return new ArrayList<>(budgets.getOrDefault(currentUser.getUid(), new ArrayList<>())); }
             @Override public void updateBudget(Budget budget) throws Exception { }
             @Override public void deleteBudget(String budgetId) throws Exception { if (currentUser == null) return; List<Budget> list = budgets.get(currentUser.getUid()); if (list != null) list.removeIf(b -> b.getBudgetId().equals(budgetId)); }
             @Override public User getUser(int userId) throws Exception { return null; }
    }
}
