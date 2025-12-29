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

import java.util.List;
import java.util.UUID;

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
        
        final EditText etCategory = new EditText(getContext());
        etCategory.setHint(R.string.category_hint);
        final EditText etAmount = new EditText(getContext());
        etAmount.setHint(R.string.budget_amount_hint);
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.addView(etCategory);
        layout.addView(etAmount);

        builder.setView(layout)
               .setTitle(R.string.add_new_budget)
               .setPositiveButton(R.string.save, (dialog, id) -> {
                   String category = etCategory.getText().toString();
                   String amountStr = etAmount.getText().toString();
                   
                   if (!category.isEmpty() && !amountStr.isEmpty()) {
                       double amount = Double.parseDouble(amountStr);
                       String budgetId = UUID.randomUUID().toString();
                       Budget b = new Budget(budgetId, category, amount, 0.0);
                       
                       FinanceManager.getInstance().addBudget(b, new FinanceManager.CustomCallback<Boolean>() {
                           @Override
                           public void onResult(Boolean result) {
                               if (getActivity() != null) {
                                   getActivity().runOnUiThread(() -> {
                                       Toast.makeText(getContext(), R.string.budget_added, Toast.LENGTH_SHORT).show();
                                       loadBudgets();
                                   });
                               }
                           }

                           @Override
                           public void onError(Exception e) {
                               if (getActivity() != null) {
                                   getActivity().runOnUiThread(() -> 
                                       Toast.makeText(getContext(), getString(R.string.error_adding_budget, e.getMessage()), Toast.LENGTH_SHORT).show()
                                   );
                               }
                           }
                       });
                   } else {
                       Toast.makeText(getContext(), R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        
        builder.create().show();
    }

    private void loadBudgets() {
        FinanceManager.getInstance().getBudgets(new FinanceManager.CustomCallback<List<Budget>>() {
            @Override
            public void onResult(List<Budget> result) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> displayBudgets(result));
            }
            @Override
            public void onError(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_loading_budgets, e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            }
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
        
        if (progress > 100) pb.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        else if (progress > 80) pb.getProgressDrawable().setColorFilter(Color.rgb(255, 165, 0), android.graphics.PorterDuff.Mode.SRC_IN);
        else pb.getProgressDrawable().setColorFilter(Color.rgb(46, 204, 113), android.graphics.PorterDuff.Mode.SRC_IN);

        LinearLayout buttonRow = createButtonRow(budget);

        content.addView(title);
        content.addView(pb);
        content.addView(buttonRow);
        
        card.addView(content);
        budgetsContainer.addView(card);
    }

    private LinearLayout createButtonRow(Budget budget) {
        LinearLayout buttonRow = new LinearLayout(getContext());
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setPadding(0, 16, 0, 0);
        
        Button editButton = new Button(getContext());
        editButton.setText(R.string.edit);
        editButton.setOnClickListener(v -> showEditBudgetDialog(budget));

        Button deleteButton = new Button(getContext());
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(v -> showDeleteConfirmation(budget));
        
        buttonRow.addView(editButton);
        buttonRow.addView(deleteButton);
        return buttonRow;
    }

    private void showEditBudgetDialog(Budget budget) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final EditText etCategory = new EditText(getContext());
        etCategory.setText(budget.getCategory());
        final EditText etAmount = new EditText(getContext());
        etAmount.setText(String.valueOf(budget.getAmount()));
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.addView(etCategory);
        layout.addView(etAmount);

        builder.setView(layout)
                .setTitle(R.string.edit_budget)
                .setPositiveButton(R.string.save, (dialog, id) -> {
                    String category = etCategory.getText().toString();
                    String amountStr = etAmount.getText().toString();

                    if (!category.isEmpty() && !amountStr.isEmpty()) {
                        double amount = Double.parseDouble(amountStr);
                        Budget updatedBudget = new Budget(budget.getBudgetId(), category, amount, budget.getSpent());
                        FinanceManager.getInstance().updateBudget(updatedBudget, new FinanceManager.CustomCallback<Boolean>() {
                            @Override
                            public void onResult(Boolean result) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(), R.string.budget_updated, Toast.LENGTH_SHORT).show();
                                        loadBudgets();
                                    });
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() ->
                                            Toast.makeText(getContext(), getString(R.string.error_updating_budget, e.getMessage()), Toast.LENGTH_SHORT).show()
                                    );
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void showDeleteConfirmation(Budget budget) {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.delete_budget)
            .setMessage(getString(R.string.delete_budget_confirmation, budget.getCategory()))
            .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteBudget(budget.getBudgetId()))
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    private void deleteBudget(String budgetId) {
        FinanceManager.getInstance().deleteBudget(budgetId, new FinanceManager.CustomCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.budget_deleted, Toast.LENGTH_SHORT).show();
                        loadBudgets(); // Refresh list
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_deleting_budget, e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
