package com.example.online_personal_finance_manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.Account;
import com.example.online_personal_finance_manager.backend.Transaction;
import com.example.online_personal_finance_manager.backend.TransactionType;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionsFragment extends Fragment {

    private LinearLayout transactionsContainer;
    private List<Transaction> allTransactions = new ArrayList<>();
    private List<Account> allAccounts = new ArrayList<>();
    
    private final ActivityResultLauncher<Intent> createDocLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        writeCsvToUri(uri);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionsContainer = view.findViewById(R.id.transactionsContainer);
        
        view.findViewById(R.id.btnAddTransaction).setOnClickListener(v -> showAddTransactionDialog());
        view.findViewById(R.id.btnExport).setOnClickListener(v -> startExportCsv());

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndDisplay(s.toString());
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        loadInitialData();
    }
    
    private void startExportCsv() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "transactions.csv");
        createDocLauncher.launch(intent);
    }
    
    private void writeCsvToUri(Uri uri) {
        try {
            OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);
            if (outputStream == null) return;
            
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write("Date,Category,Type,Amount\n"); // Header
            
            for (Transaction t : allTransactions) {
                String line = String.format(Locale.US, "%s,%s,%s,%.2f\n", 
                        t.getDate().toString(), 
                        t.getCategory(), 
                        t.getType().name(), 
                        t.getAmount());
                writer.write(line);
            }
            
            writer.close();
            Toast.makeText(getContext(), R.string.export_successful, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.export_failed, e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddTransactionDialog() {
        if (getContext() == null) return;

        if (allAccounts.isEmpty()) {
            Toast.makeText(getContext(), R.string.add_account_first, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_transaction, null);

        final EditText etAmount = dialogView.findViewById(R.id.etAmount);
        final EditText etCategory = dialogView.findViewById(R.id.etCategory);
        final Spinner spType = dialogView.findViewById(R.id.spType);
        final Spinner spAccount = new Spinner(getContext());
        
        ArrayAdapter<TransactionType> typeAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, TransactionType.values());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, 
                allAccounts.stream().map(Account::getName).collect(Collectors.toList()));
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAccount.setAdapter(accountAdapter);

        LinearLayout layout = (LinearLayout) dialogView;
        layout.addView(spAccount, 2); // Add spinner to the dialog

        builder.setView(dialogView)
               .setPositiveButton(R.string.save, (dialog, id) -> {
                   String amountStr = etAmount.getText().toString();
                   String category = etCategory.getText().toString();
                   TransactionType type = (TransactionType) spType.getSelectedItem();
                   int selectedAccountIndex = spAccount.getSelectedItemPosition();
                   
                   if (!amountStr.isEmpty() && !category.isEmpty() && selectedAccountIndex >= 0) {
                       double amount = Double.parseDouble(amountStr);
                       String accountId = allAccounts.get(selectedAccountIndex).getAccountId();
                       String transactionId = UUID.randomUUID().toString();
                       Transaction t = new Transaction(transactionId, accountId, amount, type, category, new Date());
                       FinanceManager.getInstance().addTransaction(t, new FinanceManager.CustomCallback<Boolean>() {
                           @Override
                           public void onResult(Boolean result) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        Toast.makeText(getContext(), R.string.transaction_added, Toast.LENGTH_SHORT).show();
                                        loadInitialData(); 
                                    });
                                }
                           }
                           @Override
                           public void onError(Exception e) {
                               if (getActivity() != null) {
                                   getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_adding_transaction, e.getMessage()), Toast.LENGTH_SHORT).show());
                               }
                           }
                       });
                   }
               })
               .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        
        builder.create().show();
    }

    private void loadInitialData() {
        FinanceManager.getInstance().getAccounts(new FinanceManager.CustomCallback<List<Account>>() {
            @Override
            public void onResult(List<Account> result) {
                allAccounts = result;
                loadTransactions(); // Now load transactions
            }
            @Override
            public void onError(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_loading_accounts, e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void loadTransactions() {
        FinanceManager.getInstance().getTransactions(new FinanceManager.CustomCallback<List<Transaction>>() {
            @Override
            public void onResult(List<Transaction> result) {
                allTransactions = result;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> displayTransactions(allTransactions));
                }
            }
            @Override
            public void onError(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.error_loading_transactions, e.getMessage()), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void filterAndDisplay(String query) {
        List<Transaction> filteredList;
        if (query.isEmpty()) {
            filteredList = allTransactions;
        } else {
            filteredList = allTransactions.stream()
                .filter(t -> t.getCategory().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        }
        displayTransactions(filteredList);
    }

    private void displayTransactions(List<Transaction> transactions) {
        transactionsContainer.removeAllViews();
        for (Transaction t : transactions) {
            addTransactionRow(t);
        }
    }

    private void addTransactionRow(Transaction t) {
        if(getContext() == null) return;
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(12, 24, 12, 24);
        
        TextView tvDate = new TextView(getContext());
        tvDate.setText(t.getDate().toString().substring(4, 10));
        tvDate.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f));
        
        TextView tvDesc = new TextView(getContext());
        String accountName = allAccounts.stream()
            .filter(a -> a.getAccountId().equals(t.getAccountId()))
            .map(Account::getName)
            .findFirst()
            .orElse("N/A");
        tvDesc.setText(getString(R.string.transaction_description, t.getCategory(), accountName));
        tvDesc.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
        
        TextView tvAmount = new TextView(getContext());
        tvAmount.setText(String.format(Locale.US, "₹%.2f", t.getAmount()));
        tvAmount.setGravity(android.view.Gravity.END);
        if (t.getType() == TransactionType.INCOME) {
            tvAmount.setTextColor(Color.parseColor("#2ECC71"));
        } else {
            tvAmount.setTextColor(Color.parseColor("#E74C3C"));
        }
        tvAmount.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f));
        
        row.addView(tvDate);
        row.addView(tvDesc);
        row.addView(tvAmount);
        
        View line = new View(getContext());
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.LTGRAY);
        
        transactionsContainer.addView(row);
        transactionsContainer.addView(line);
    }
}
