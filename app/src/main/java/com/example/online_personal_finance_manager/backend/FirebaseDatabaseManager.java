package com.example.online_personal_finance_manager.backend;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirebaseDatabaseManager implements DatabaseManager {

    private final FirebaseAuth mAuth;
    private final FirebaseDatabase mDatabase;

    public FirebaseDatabaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    private String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    @Override
    public User login(String email, String password) throws ExecutionException, InterruptedException {
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
        Tasks.await(task);
        FirebaseUser firebaseUser = task.getResult().getUser();
        if (firebaseUser != null) {
            return new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), null);
        }
        return null;
    }

    @Override
    public void addUser(User user) throws ExecutionException, InterruptedException {
        Task<AuthResult> task = mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword());
        Tasks.await(task);
    }

    @Override
    public void addTransaction(Transaction transaction) {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("transactions");
        String key = dbRef.push().getKey();
        transaction.transactionId = key;
        dbRef.child(key).setValue(transaction);
    }

    @Override
    public List<Transaction> getTransactions(int userId) throws ExecutionException, InterruptedException {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("transactions");
        Task<DataSnapshot> task = dbRef.get();
        DataSnapshot snapshot = Tasks.await(task);
        List<Transaction> transactions = new ArrayList<>();
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            Transaction transaction = postSnapshot.getValue(Transaction.class);
            transactions.add(transaction);
        }
        return transactions;
    }

    @Override
    public void addAccount(Account account) {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("accounts");
        String key = dbRef.push().getKey();
        account.accountId = key;
        dbRef.child(key).setValue(account);
    }

    @Override
    public List<Account> getAccounts(int userId) throws ExecutionException, InterruptedException {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("accounts");
        Task<DataSnapshot> task = dbRef.get();
        DataSnapshot snapshot = Tasks.await(task);
        List<Account> accounts = new ArrayList<>();
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            Account account = postSnapshot.getValue(Account.class);
            accounts.add(account);
        }
        return accounts;
    }
    
    @Override
    public void deleteAccount(String accountId) {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("accounts").child(accountId);
        dbRef.removeValue();
    }
    
    @Override
    public void addBudget(Budget budget) {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("budgets");
        String key = dbRef.push().getKey();
        budget.budgetId = key;
        dbRef.child(key).setValue(budget);
    }

    @Override
    public List<Budget> getBudgets(int userId) throws ExecutionException, InterruptedException {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("budgets");
        Task<DataSnapshot> task = dbRef.get();
        DataSnapshot snapshot = Tasks.await(task);
        List<Budget> budgets = new ArrayList<>();
        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
            Budget budget = postSnapshot.getValue(Budget.class);
            budgets.add(budget);
        }
        return budgets;
    }
    
    @Override
    public void deleteBudget(String budgetId) {
        DatabaseReference dbRef = mDatabase.getReference("users").child(getCurrentUserId()).child("budgets").child(budgetId);
        dbRef.removeValue();
    }
    
    // Omitting other interface methods for brevity
    @Override
    public User getUser(int userId) { return null; }
    @Override
    public void updateAccount(Account account) {}
    @Override
    public void updateBudget(Budget budget) {}
}
