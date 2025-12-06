package com.example.online_personal_finance_manager;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.Account;
import com.example.online_personal_finance_manager.backend.FinanceManager;

import java.util.List;

public class AccountsFragment extends Fragment {

    private LinearLayout accountsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountsContainer = view.findViewById(R.id.accountsContainer);
        view.findViewById(R.id.btnAddAccount).setOnClickListener(v -> showAddAccountDialog());

        loadAccounts();
    }

    private void showAddAccountDialog() { /* ... unchanged ... */ }

    private void loadAccounts() {
        FinanceManager.getInstance().getAccounts(new FinanceManager.Callback<List<Account>>() {
            @Override
            public void onResult(List<Account> result) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> displayAccounts(result));
                }
            }

            @Override
            public void onError(Exception e) { /* ... */ }
        });
    }

    private void displayAccounts(List<Account> accounts) {
        accountsContainer.removeAllViews();
        for (Account account : accounts) {
            addAccountCard(account);
        }
    }

    private void addAccountCard(Account account) {
        if (getContext() == null) return;
        CardView card = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        card.setRadius(16);

        LinearLayout content = new LinearLayout(getContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(32, 32, 32, 32);

        TextView title = new TextView(getContext());
        title.setText(account.getName());
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView balance = new TextView(getContext());
        balance.setText(String.format("Balance: ₹%.2f", account.getBalance()));
        balance.setPadding(0, 8, 0, 16);

        LinearLayout buttonRow = new LinearLayout(getContext());
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);

        Button deleteButton = new Button(getContext());
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> showDeleteConfirmation(account));
        
        buttonRow.addView(deleteButton);
        
        content.addView(title);
        content.addView(balance);
        content.addView(buttonRow);

        card.addView(content);
        accountsContainer.addView(card);
    }

    private void showDeleteConfirmation(Account account) {
        new AlertDialog.Builder(getContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete '" + account.getName() + "'?")
            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                deleteAccount(account.getAccountId());
            })
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void deleteAccount(String accountId) {
        FinanceManager.getInstance().deleteAccount(accountId, new FinanceManager.Callback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                        loadAccounts(); // Refresh the list
                    });
                }
            }
            @Override
            public void onError(Exception e) { /* ... */ }
        });
    }
}
