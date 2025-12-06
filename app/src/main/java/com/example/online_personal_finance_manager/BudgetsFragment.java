package com.example.online_personal_finance_manager;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.Budget;
import com.example.online_personal_finance_manager.backend.FinanceManager;

import java.util.Calendar;
import java.util.List;

public class BudgetsFragment extends Fragment {

    private LinearLayout budgetsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budgets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        budgetsContainer = view.findViewById(R.id.budgetsContainer);
        view.findViewById(R.id.btnAddBudget).setOnClickListener(v -> showAddBudgetDialog());
        loadBudgets();
    }

    private void showAddBudgetDialog() {
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_budget, null);
        
        final EditText etCategory = dialogView.findViewById(R.id.etCategory);
        final EditText etAmount = dialogView.findViewById(R.id.etAmount);
        
        builder.setView(dialogView)
               .setPositiveButton("Save", (dialog, id) -> {
                   String category = etCategory.getText().toString();
                   String amountStr = etAmount.getText().toString();
                   
                   if (!category.isEmpty() && !amountStr.isEmpty()) {
                       double amount = Double.parseDouble(amountStr);
                       // ID handled by Firebase
                       Budget b = new Budget(null, category, amount, 0.0);
                       
                       FinanceManager.getInstance().addBudget(b, new FinanceManager.Callback<Boolean>() {
                           @Override
                           public void onResult(Boolean result) {
                               if (getActivity() != null) {
                                   getActivity().runOnUiThread(() -> {
                                       Toast.makeText(getContext(), "Budget Added", Toast.LENGTH_SHORT).show();
                                       loadBudgets();
                                   });
                               }
                           }

                           @Override
                           public void onError(Exception e) {
                               if (getActivity() != null) {
                                   getActivity().runOnUiThread(() -> 
                                       Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                   );
                               }
                           }
                       });
                   } else {
                       Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        
        builder.create().show();
    }

    private void loadBudgets() {
        FinanceManager.getInstance().getBudgets(new FinanceManager.Callback<List<Budget>>() {
            @Override
            public void onResult(List<Budget> result) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> displayBudgets(result));
            }
            @Override
            public void onError(Exception e) {}
        });
    }

    private void displayBudgets(List<Budget> budgets) {
        budgetsContainer.removeAllViews();
        for (Budget b : budgets) {
            addBudgetCard(b);
        }
    }

    private void addBudgetCard(Budget budget) {
        if(getContext() == null) return;
        CardView card = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        card.setRadius(16);
        card.setCardElevation(4);

        LinearLayout content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(32, 32, 32, 32);

        TextView title = new TextView(getContext());
        title.setText(budget.getCategory());
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        ProgressBar pb = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax(100);
        double ratio = budget.getAmount() > 0 ? budget.getSpent() / budget.getAmount() : 0;
        int progress = (int) (ratio * 100);
        pb.setProgress(progress);
        pb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 24));
        
        // Basic progress coloring
        if (progress > 100) pb.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        else if (progress > 80) pb.getProgressDrawable().setColorFilter(Color.rgb(255, 165, 0), android.graphics.PorterDuff.Mode.SRC_IN);
        else pb.getProgressDrawable().setColorFilter(Color.rgb(46, 204, 113), android.graphics.PorterDuff.Mode.SRC_IN);

        // --- Buttons Row ---
        LinearLayout buttonRow = new LinearLayout(getContext());
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, 16, 0, 0);
        
        Button deleteButton = new Button(getContext());
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> showDeleteConfirmation(budget));
        
        buttonRow.addView(deleteButton);

        content.addView(title);
        content.addView(pb);
        content.addView(buttonRow);
        
        card.addView(content);
        budgetsContainer.addView(card);
    }
    
    private void showDeleteConfirmation(Budget budget) {
        new AlertDialog.Builder(getContext())
            .setTitle("Delete Budget")
            .setMessage("Are you sure you want to delete the budget for '" + budget.getCategory() + "'?")
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                deleteBudget(budget.getBudgetId());
            })
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    private void deleteBudget(String budgetId) {
        FinanceManager.getInstance().deleteBudget(budgetId, new FinanceManager.Callback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Budget deleted", Toast.LENGTH_SHORT).show();
                        loadBudgets(); // Refresh list
                    });
                }
            }
            @Override
            public void onError(Exception e) {}
        });
    }
}
