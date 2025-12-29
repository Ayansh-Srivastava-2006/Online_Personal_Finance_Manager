package com.example.online_personal_finance_manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.Transaction;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvMonthlyIncome, tvMonthlyExpenses, tvSavingsRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMonthlyIncome = view.findViewById(R.id.tvMonthlyIncome);
        tvMonthlyExpenses = view.findViewById(R.id.tvMonthlyExpenses);
        tvSavingsRate = view.findViewById(R.id.tvSavingsRate);

        loadData();
    }

    private void loadData() {
        FinanceManager.getInstance().getTransactions(new FinanceManager.CustomCallback<List<Transaction>>() {
            @Override
            public void onResult(List<Transaction> transactions) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    if (getView() == null) return; // Ensure fragment view is available

                    if (transactions == null || transactions.isEmpty()) {
                        // Handle empty state, maybe show zeros
                        tvMonthlyIncome.setText(String.format(Locale.US, "₹%.2f", 0.0));
                        tvMonthlyExpenses.setText(String.format(Locale.US, "₹%.2f", 0.0));
                        tvSavingsRate.setText(String.format(Locale.US, "%.2f%%", 0.0));
                        return;
                    }

                    FinanceManager.FinancialSummary summary = FinanceManager.getInstance().calculateSummary(transactions);

                    tvMonthlyIncome.setText(String.format(Locale.US, "₹%.2f", summary.totalIncome));
                    tvMonthlyExpenses.setText(String.format(Locale.US, "₹%.2f", summary.totalExpenses));
                    
                    double savingsRate = summary.totalIncome > 0 ? (summary.netSavings / summary.totalIncome) * 100 : 0;
                    tvSavingsRate.setText(String.format(Locale.US, "%.2f%%", savingsRate));
                });
            }

            @Override
            public void onError(Exception e) {
                 if (getActivity() == null) return;
                 
                 getActivity().runOnUiThread(() -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), getString(R.string.error_loading_dashboard_data, e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
