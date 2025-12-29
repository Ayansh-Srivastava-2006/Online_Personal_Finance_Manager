package com.example.online_personal_finance_manager.backend;

import java.util.Date;

public class Transaction {
    public String transactionId;
    public String accountId;
    public double amount;
    public TransactionType type;
    public String category;
    public long date;

    public Transaction(String transactionId, String accountId, double amount, TransactionType type, String category, Date date) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date.getTime();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
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
