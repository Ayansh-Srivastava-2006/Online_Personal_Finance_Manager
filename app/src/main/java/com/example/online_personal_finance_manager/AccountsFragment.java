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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.Account;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    private void showAddAccountDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        final EditText etAccountName = new EditText(getContext());
        etAccountName.setHint(R.string.account_name_hint);
        final EditText etAccountType = new EditText(getContext());
        etAccountType.setHint(R.string.account_type_hint);
        final EditText etBalance = new EditText(getContext());
        etBalance.setHint(R.string.initial_balance_hint);
        etBalance.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.addView(etAccountName);
        layout.addView(etAccountType);
        layout.addView(etBalance);

        builder.setView(layout)
            .setTitle(R.string.add_new_account)
            .setPositiveButton(R.string.add, (dialog, id) -> {
                String name = etAccountName.getText().toString();
                String type = etAccountType.getText().toString();
                String balanceStr = etBalance.getText().toString();
                if (!name.isEmpty() && !type.isEmpty() && !balanceStr.isEmpty()) {
                    double balance = Double.parseDouble(balanceStr);
                    String accountId = UUID.randomUUID().toString();
                    Account newAccount = new Account(accountId, name, type, balance);
                    FinanceManager.getInstance().addAccount(newAccount, new FinanceManager.CustomCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            if(getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), R.string.account_added, Toast.LENGTH_SHORT).show();
                                    loadAccounts();
                                });
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                             if(getActivity() != null) {
                                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_adding_account, e.getMessage()), Toast.LENGTH_SHORT).show());
                            }
                        }
                    });
                }
            })
            .setNegativeButton(R.string.cancel, null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadAccounts() {
        FinanceManager.getInstance().getAccounts(new FinanceManager.CustomCallback<List<Account>>() {
            @Override
            public void onResult(List<Account> result) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> displayAccounts(result));
                }
            }

            @Override
            public void onError(Exception e) {
                 if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_loading_accounts, e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            }
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
        balance.setText(String.format(Locale.US, "Balance: ₹%.2f", account.getBalance()));
        balance.setPadding(0, 8, 0, 16);

        LinearLayout buttonRow = createButtonRow(account);
        
        content.addView(title);
        content.addView(balance);
        content.addView(buttonRow);

        card.addView(content);
        accountsContainer.addView(card);
    }

    private LinearLayout createButtonRow(Account account) {
        LinearLayout buttonRow = new LinearLayout(getContext());
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(getContext());
        editButton.setText(R.string.edit);
        editButton.setOnClickListener(v -> showEditAccountDialog(account));

        Button deleteButton = new Button(getContext());
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(v -> showDeleteConfirmation(account));
        
        buttonRow.addView(editButton);
        buttonRow.addView(deleteButton);
        return buttonRow;
    }

    private void showEditAccountDialog(Account account) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final EditText etAccountName = new EditText(getContext());
        etAccountName.setText(account.getName());
        final EditText etAccountType = new EditText(getContext());
        etAccountType.setText(account.getType());
        final EditText etBalance = new EditText(getContext());
        etBalance.setText(String.valueOf(account.getBalance()));
        etBalance.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.addView(etAccountName);
        layout.addView(etAccountType);
        layout.addView(etBalance);

        builder.setView(layout)
                .setTitle(R.string.edit_account)
                .setPositiveButton(R.string.save, (dialog, id) -> {
                    String name = etAccountName.getText().toString();
                    String type = etAccountType.getText().toString();
                    String balanceStr = etBalance.getText().toString();
                    if (!name.isEmpty() && !type.isEmpty() && !balanceStr.isEmpty()) {
                        double balance = Double.parseDouble(balanceStr);
                        Account updatedAccount = new Account(account.getAccountId(), name, type, balance);
                        FinanceManager.getInstance().updateAccount(updatedAccount, new FinanceManager.CustomCallback<Boolean>() {
                            @Override
                            public void onResult(Boolean result) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(), R.string.account_updated, Toast.LENGTH_SHORT).show();
                                        loadAccounts();
                                    });
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_updating_account, e.getMessage()), Toast.LENGTH_SHORT).show());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showDeleteConfirmation(Account account) {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.delete_account)
            .setMessage(getString(R.string.delete_account_confirmation, account.getName()))
            .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteAccount(account.getAccountId()))
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void deleteAccount(String accountId) {
        FinanceManager.getInstance().deleteAccount(accountId, new FinanceManager.CustomCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.account_deleted, Toast.LENGTH_SHORT).show();
                        loadAccounts(); // Refresh the list
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_deleting_account, e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
