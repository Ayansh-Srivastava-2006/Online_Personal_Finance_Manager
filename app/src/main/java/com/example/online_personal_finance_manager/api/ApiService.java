package com.example.online_personal_finance_manager.api;

import com.example.online_personal_finance_manager.backend.Account;
import com.example.online_personal_finance_manager.backend.Budget;
import com.example.online_personal_finance_manager.backend.Transaction;
import com.example.online_personal_finance_manager.backend.TransactionType;
import com.example.online_personal_finance_manager.backend.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface ApiService {
    @POST("register")
    Call<UserResponse> register(@Body RegisterRequest request);

    @POST("login")
    Call<UserResponse> login(@Body LoginRequest request);

    @GET("accounts")
    Call<List<AccountResponse>> getAccounts();

    @POST("accounts")
    Call<AccountResponse> addAccount(@Body AccountRequest request);

    @GET("transactions")
    Call<List<TransactionResponse>> getTransactions();

    @POST("transactions")
    Call<TransactionResponse> addTransaction(@Body TransactionRequest request);

    @GET("budgets")
    Call<List<BudgetResponse>> getBudgets();

    @POST("budgets")
    Call<BudgetResponse> addBudget(@Body BudgetRequest request);

    // Request/Response classes
    class RegisterRequest {
        String fullName;
        String username;
        String email;
        String password;

        public RegisterRequest(String fullName, String username, String email, String password) {
            this.fullName = fullName;
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }

    class LoginRequest {
        String email;
        String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class UserResponse {
        String uid;
        String fullName;
        String username;
        String email;

        public User toUser() {
            return new User(uid, fullName, username, email, null);
        }
    }

    class ErrorResponse {
        String error;
    }

    class AccountRequest {
        String accountId;
        String name;
        String type;
        Double balance;

        public AccountRequest(String accountId, String name, String type, double balance) {
            this.accountId = accountId;
            this.name = name;
            this.type = type;
            this.balance = balance;
        }
    }

    class AccountResponse {
        String accountId;
        String name;
        String type;
        double balance;

        public Account toAccount() {
            return new Account(accountId, name, type, balance);
        }
    }

    class TransactionRequest {
        String transactionId;
        String accountId;
        double amount;
        String type;
        String category;
        Long date;

        public TransactionRequest(String transactionId, String accountId, double amount, String type, String category, long date) {
            this.transactionId = transactionId;
            this.accountId = accountId;
            this.amount = amount;
            this.type = type;
            this.category = category;
            this.date = date;
        }
    }

    class TransactionResponse {
        String transactionId;
        String accountId;
        double amount;
        String type;
        String category;
        long date;

        public Transaction toTransaction() {
            return new Transaction(transactionId, accountId, amount, 
                TransactionType.valueOf(type), category, new java.util.Date(date));
        }
    }

    class BudgetRequest {
        String budgetId;
        String category;
        Double amount;
        Double spent;

        public BudgetRequest(String budgetId, String category, double amount, double spent) {
            this.budgetId = budgetId;
            this.category = category;
            this.amount = amount;
            this.spent = spent;
        }
    }

    class BudgetResponse {
        String budgetId;
        String category;
        double amount;
        double spent;

        public Budget toBudget() {
            return new Budget(budgetId, category, amount, spent);
        }
    }
}

