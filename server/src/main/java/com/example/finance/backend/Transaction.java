package com.example.finance.backend;

import java.util.Date;

public class Transaction {
    private String transactionId;
    private String accountId;
    private double amount;
    private TransactionType type;
    private String category;
    private long date;

    public Transaction() {
    }

    public Transaction(String transactionId, String accountId, double amount, TransactionType type, String category,
            Date date) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date != null ? date.getTime() : System.currentTimeMillis();
    }

    public Transaction(String transactionId, String accountId, double amount, TransactionType type, String category,
            long dateMillis) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = dateMillis;
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

    public long getDate() {
        return date;
    }

    public Date getDateAsDate() {
        return new Date(date);
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
