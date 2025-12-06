package com.example.online_personal_finance_manager.backend;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Account {
    public String accountId;
    public String name;
    public String type; // "Savings", "Checking", "Credit Card", etc.
    public double balance;

    public Account() {
    }

    public Account(String accountId, String name, String type, double balance) {
        this.accountId = accountId;
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public String getAccountId() { return accountId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getBalance() { return balance; }
    
    public void setBalance(double balance) { this.balance = balance; }
}
