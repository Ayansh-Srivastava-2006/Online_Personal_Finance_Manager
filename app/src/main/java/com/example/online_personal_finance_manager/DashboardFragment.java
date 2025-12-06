package com.example.online_personal_finance_manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.FinanceManager;
import com.example.online_personal_finance_manager.backend.Transaction;
import com.example.online_personal_finance_manager.charts.LineChartView;
import com.example.online_personal_finance_manager.charts.PieChartView;

import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView tvTotalBalance, tvMonthlyIncome, tvMonthlyExpenses, tvSavingsRate;
    private LineChartView lineChart;
    private PieChartView pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvMonthlyIncome = view.findViewById(R.id.tvMonthlyIncome);
        tvMonthlyExpenses = view.findViewById(R.id.tvMonthlyExpenses);
        tvSavingsRate = view.findViewById(R.id.tvSavingsRate);
        lineChart = view.findViewById(R.id.lineChart);
        pieChart = view.findViewById(R.id.pieChart);

        loadData();
    }

    private void loadData() {
        FinanceManager.getInstance().getTransactions(new FinanceManager.Callback<List<Transaction>>() {
            @Override
            public void onResult(List<Transaction> transactions) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Update summary cards and charts
                        // This logic remains the same as it operates on the list of transactions
                        // ...
                    });
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }
}
