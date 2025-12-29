package com.example.online_personal_finance_manager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.Transaction;
import com.example.online_personal_finance_manager.backend.TransactionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsFragment extends Fragment {

    private TextView tvTotalIncome, tvTotalExpense, tvNetSavings;
    private LinearLayout breakdownContainer;
    private List<Transaction> currentTransactions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        tvNetSavings = view.findViewById(R.id.tvNetSavings);
        breakdownContainer = view.findViewById(R.id.breakdownContainer);

        view.findViewById(R.id.btnPrint).setOnClickListener(v -> printReport());

        loadReportData();
    }

    private void loadReportData() {
        FinanceManager.getInstance().getTransactions(new FinanceManager.CustomCallback<List<Transaction>>() {
            @Override
            public void onResult(List<Transaction> result) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        currentTransactions = result;
                        processAndDisplayData(result);
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_loading_report_data, e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void processAndDisplayData(List<Transaction> transactions) {
        double income = 0;
        double expense = 0;
        Map<String, Double> categoryExpenses = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
                Double currentAmount = categoryExpenses.get(t.getCategory());
                double newAmount = (currentAmount != null ? currentAmount : 0.0) + t.getAmount();
                categoryExpenses.put(t.getCategory(), newAmount);
            }
        }

        tvTotalIncome.setText(String.format(Locale.US, "₹%.2f", income));
        tvTotalExpense.setText(String.format(Locale.US, "₹%.2f", expense));
        tvNetSavings.setText(String.format(Locale.US, "₹%.2f", income - expense));

        breakdownContainer.removeAllViews();
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            addBreakdownRow(entry.getKey(), entry.getValue());
        }
    }

    private void addBreakdownRow(String category, double amount) {
        if(getContext() == null) return;
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(12, 24, 12, 24);

        TextView tvCat = new TextView(getContext());
        tvCat.setText(category);
        tvCat.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));

        TextView tvAmt = new TextView(getContext());
        tvAmt.setText(String.format(Locale.US, "₹%.2f", amount));
        tvAmt.setGravity(android.view.Gravity.END);
        tvAmt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        row.addView(tvCat);
        row.addView(tvAmt);

        View line = new View(getContext());
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.LTGRAY);

        breakdownContainer.addView(row);
        breakdownContainer.addView(line);
    }

    private void printReport() {
        if (getContext() == null || currentTransactions.isEmpty()) {
            Toast.makeText(getContext(), R.string.no_data_to_print, Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate HTML string
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h1>Financial Report</h1>");
        html.append("<p>Generated by Finance Manager App</p>");
        html.append("<hr>");
        
        double income = 0;
        double expense = 0;
        Map<String, Double> categoryExpenses = new HashMap<>();
        for (Transaction t : currentTransactions) {
            if (t.getType() == TransactionType.INCOME) income += t.getAmount();
            else {
                expense += t.getAmount();
                Double currentAmount = categoryExpenses.get(t.getCategory());
                double newAmount = (currentAmount != null ? currentAmount : 0.0) + t.getAmount();
                categoryExpenses.put(t.getCategory(), newAmount);
            }
        }
        
        html.append("<h2>Summary</h2>");
        html.append("<p><strong>Total Income:</strong> ₹").append(String.format(Locale.US, "%.2f", income)).append("</p>");
        html.append("<p><strong>Total Expenses:</strong> ₹").append(String.format(Locale.US, "%.2f", expense)).append("</p>");
        html.append("<p><strong>Net Savings:</strong> ₹").append(String.format(Locale.US, "%.2f", income - expense)).append("</p>");
        
        html.append("<h2>Expense Breakdown</h2>");
        html.append("<table border='1' style='width:100%; border-collapse: collapse;'>");
        html.append("<tr><th>Category</th><th>Amount</th></tr>");
        
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
             html.append("<tr>");
             html.append("<td style='padding: 8px;'>").append(entry.getKey()).append("</td>");
             html.append("<td style='padding: 8px; text-align: right;'>₹").append(String.format(Locale.US, "%.2f", entry.getValue())).append("</td></tr>");
        }
        html.append("</table></body></html>");

        WebView webView = new WebView(getContext());
        webView.loadDataWithBaseURL(null, html.toString(), "text/html", "UTF-8", null);

        PrintManager printManager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("FinanceReport");
        String jobName = getString(R.string.app_name) + " Document";
        
        try {
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error printing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
