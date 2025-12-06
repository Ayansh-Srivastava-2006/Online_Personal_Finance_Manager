package com.example.online_personal_finance_manager.backend;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Transaction {
    public String transactionId;
    public double amount;
    public TransactionType type;
    public String category;
    public long date;

    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    public Transaction(String transactionId, double amount, TransactionType type, String category, Date date) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date.getTime();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return new Date(date);
    }
}
