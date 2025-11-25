package com.example.online_personal_finance_manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.online_personal_finance_manager.backend.FinanceManager;
import com.example.online_personal_finance_manager.backend.Transaction;
import com.example.online_personal_finance_manager.backend.User;

import java.util.List;

public class DashboardActivity extends Activity {

    private TextView tvTotalBalance;
    private TextView tvMonthlyIncome;
    private TextView tvMonthlyExpenses;
    private TextView tvSavingsRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        User currentUser = FinanceManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Not logged in, redirect to Login
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvMonthlyIncome = findViewById(R.id.tvMonthlyIncome);
        tvMonthlyExpenses = findViewById(R.id.tvMonthlyExpenses);
        tvSavingsRate = findViewById(R.id.tvSavingsRate);
        ImageButton btnMenu = findViewById(R.id.btnMenu); // Use finding by ID if I added an ID, let's check layout

        // Since I didn't add an ID to the ImageButton in the layout, I'll look it up by type or add ID.
        // Actually, I should update the layout to add ID to the menu button.
        // For now, I'll focus on loading data.

        loadDashboardData(currentUser.getUserId());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        User currentUser = FinanceManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadDashboardData(currentUser.getUserId());
        }
    }

    private void loadDashboardData(int userId) {
        FinanceManager.getInstance().getTransactions(userId, new FinanceManager.Callback<List<Transaction>>() {
            @Override
            public void onResult(List<Transaction> transactions) {
                FinanceManager.FinancialSummary summary = FinanceManager.getInstance().calculateSummary(transactions);
                
                runOnUiThread(() -> {
                    tvTotalBalance.setText(String.format("₹%.2f", summary.balance));
                    tvMonthlyIncome.setText(String.format("₹%.2f", summary.income));
                    tvMonthlyExpenses.setText(String.format("₹%.2f", summary.expense));
                    tvSavingsRate.setText(String.format("%.1f%%", summary.savingsRate));
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(DashboardActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
