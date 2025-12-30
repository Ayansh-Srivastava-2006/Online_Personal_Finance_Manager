package com.example.online_personal_finance_manager;

import com.example.online_personal_finance_manager.api.ApiClient;
import com.example.online_personal_finance_manager.api.ApiService;
import com.example.online_personal_finance_manager.backend.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinanceManager {

    private static final FinanceManager instance = new FinanceManager();
    private final ApiService apiService;
    private User currentUser;

    private FinanceManager() {
        apiService = ApiClient.getApiService();
    }

    public static FinanceManager getInstance() {
        return instance;
    }

    public interface CustomCallback<T> {
        void onResult(T result);
        void onError(Exception e);
    }

    public void register(String fullName, String username, String email, String password, CustomCallback<User> callback) {
        try {
            ApiService.RegisterRequest request = new ApiService.RegisterRequest(fullName, username, email, password);
            Call<ApiService.UserResponse> call = apiService.register(request);
            
            call.enqueue(new Callback<ApiService.UserResponse>() {
                @Override
                public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body().toUser();
                        currentUser = user;
                        callback.onResult(user);
                    } else {
                        String errorMsg = "Registration failed";
                        if (response.errorBody() != null) {
                            try {
                                // Try to parse error response
                                errorMsg = response.errorBody().string();
                            } catch (Exception e) {
                                errorMsg = "Registration failed: HTTP " + response.code();
                            }
                        } else {
                            errorMsg = "Registration failed: HTTP " + response.code();
                        }
                        callback.onError(new Exception(errorMsg));
                    }
                }

                @Override
                public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                    String errorMsg = "Network error: " + t.getMessage();
                    if (t.getCause() != null) {
                        errorMsg += " (" + t.getCause().getMessage() + ")";
                    }
                    callback.onError(new Exception(errorMsg, t));
                }
            });
        } catch (Exception e) {
            callback.onError(new Exception("Failed to create registration request: " + e.getMessage(), e));
        }
    }

    public void login(String email, String password, CustomCallback<User> callback) {
        ApiService.LoginRequest request = new ApiService.LoginRequest(email, password);
        Call<ApiService.UserResponse> call = apiService.login(request);
        
        call.enqueue(new Callback<ApiService.UserResponse>() {
            @Override
            public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().toUser();
                    currentUser = user;
                    callback.onResult(user);
                } else {
                    String errorMsg = "Invalid email or password";
                    if (response.errorBody() != null) {
                        try {
                            // Try to parse error response
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg = "Login failed: " + response.code();
                        }
                    }
                    callback.onError(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public void getTransactions(CustomCallback<List<Transaction>> callback) {
        Call<List<ApiService.TransactionResponse>> call = apiService.getTransactions();
        
        call.enqueue(new Callback<List<ApiService.TransactionResponse>>() {
            @Override
            public void onResponse(Call<List<ApiService.TransactionResponse>> call, 
                                 Response<List<ApiService.TransactionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> transactions = new java.util.ArrayList<>();
                    for (ApiService.TransactionResponse transactionResponse : response.body()) {
                        transactions.add(transactionResponse.toTransaction());
                    }
                    callback.onResult(transactions);
                } else {
                    String errorMsg = "Failed to retrieve transactions";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg = "Failed to retrieve transactions: HTTP " + response.code();
                        }
                    }
                    callback.onError(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.TransactionResponse>> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void addTransaction(Transaction transaction, CustomCallback<Boolean> callback) {
        ApiService.TransactionRequest request = new ApiService.TransactionRequest(
            transaction.getTransactionId(),
            transaction.getAccountId(),
            transaction.getAmount(),
            transaction.getType().name(),
            transaction.getCategory(),
            transaction.getDate().getTime()
        );
        Call<ApiService.TransactionResponse> call = apiService.addTransaction(request);
        
        call.enqueue(new Callback<ApiService.TransactionResponse>() {
            @Override
            public void onResponse(Call<ApiService.TransactionResponse> call, 
                                 Response<ApiService.TransactionResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else {
                    String errorMsg = "Failed to add transaction";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg = "Failed to add transaction: HTTP " + response.code();
                        }
                    }
                    callback.onError(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiService.TransactionResponse> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void getAccounts(CustomCallback<List<Account>> callback) {
        Call<List<ApiService.AccountResponse>> call = apiService.getAccounts();
        
        call.enqueue(new Callback<List<ApiService.AccountResponse>>() {
            @Override
            public void onResponse(Call<List<ApiService.AccountResponse>> call, 
                                 Response<List<ApiService.AccountResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Account> accounts = new java.util.ArrayList<>();
                    for (ApiService.AccountResponse accountResponse : response.body()) {
                        accounts.add(accountResponse.toAccount());
                    }
                    callback.onResult(accounts);
                } else {
                    String errorMsg = "Failed to retrieve accounts";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg = "Failed to retrieve accounts: HTTP " + response.code();
                        }
                    }
                    callback.onError(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.AccountResponse>> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void addAccount(Account account, CustomCallback<Boolean> callback) {
        ApiService.AccountRequest request = new ApiService.AccountRequest(
            account.getAccountId(), 
            account.getName(), 
            account.getType(), 
            account.getBalance()
        );
        Call<ApiService.AccountResponse> call = apiService.addAccount(request);
        
        call.enqueue(new Callback<ApiService.AccountResponse>() {
            @Override
            public void onResponse(Call<ApiService.AccountResponse> call, 
                                 Response<ApiService.AccountResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else {
                    String errorMsg = "Failed to add account";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg = "Failed to add account: HTTP " + response.code();
                        }
                    }
                    callback.onError(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiService.AccountResponse> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void updateAccount(Account account, CustomCallback<Boolean> callback) {
        // Account updates should be handled through the API in the future
        callback.onError(new Exception("Account update not yet implemented via API"));
    }

    public void deleteAccount(String accountId, CustomCallback<Boolean> callback) {
        // Account deletion should be handled through the API in the future
        callback.onError(new Exception("Account deletion not yet implemented via API"));
    }

    public void getBudgets(CustomCallback<List<Budget>> callback) {
        Call<List<ApiService.BudgetResponse>> call = apiService.getBudgets();
        
        call.enqueue(new Callback<List<ApiService.BudgetResponse>>() {
            @Override
            public void onResponse(Call<List<ApiService.BudgetResponse>> call, 
                                 Response<List<ApiService.BudgetResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Budget> budgets = new java.util.ArrayList<>();
                    for (ApiService.BudgetResponse budgetResponse : response.body()) {
                        budgets.add(budgetResponse.toBudget());
                    }
                    callback.onResult(budgets);
                } else {
                    String errorMsg = "Failed to retrieve budgets";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg = "Failed to retrieve budgets: HTTP " + response.code();
                        }
                    }
                    callback.onError(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.BudgetResponse>> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void addBudget(Budget budget, CustomCallback<Boolean> callback) {
        ApiService.BudgetRequest request = new ApiService.BudgetRequest(
            budget.getBudgetId(),
            budget.getCategory(),
            budget.getAmount(),
            budget.getSpent()
        );
        Call<ApiService.BudgetResponse> call = apiService.addBudget(request);
        
        call.enqueue(new Callback<ApiService.BudgetResponse>() {
            @Override
            public void onResponse(Call<ApiService.BudgetResponse> call, 
                                 Response<ApiService.BudgetResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else {
                    String errorMsg = "Failed to add budget";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg = "Failed to add budget: HTTP " + response.code();
                        }
                    }
                    callback.onError(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiService.BudgetResponse> call, Throwable t) {
                callback.onError(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    public void updateBudget(Budget budget, CustomCallback<Boolean> callback) {
        // Budget updates should be handled through the API in the future
        callback.onError(new Exception("Budget update not yet implemented via API"));
    }

    public void deleteBudget(String budgetId, CustomCallback<Boolean> callback) {
        // Budget deletion should be handled through the API in the future
        callback.onError(new Exception("Budget deletion not yet implemented via API"));
    }
    
    public static class FinancialSummary {
        public double totalIncome;
        public double totalExpenses;
        public double netSavings;

        public FinancialSummary(double totalIncome, double totalExpenses) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.netSavings = totalIncome - totalExpenses;
        }
    }
    
    public FinancialSummary calculateSummary(List<Transaction> transactions) {
        double income = 0;
        double expenses = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                income += t.getAmount();
            } else {
                expenses += t.getAmount();
            }
        }
        return new FinancialSummary(income, expenses);
    }
}
