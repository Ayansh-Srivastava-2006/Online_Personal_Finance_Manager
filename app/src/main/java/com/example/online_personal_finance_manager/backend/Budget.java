package com.example.online_personal_finance_manager.backend;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Budget {
    public String budgetId;
    public String category;
    public double amount;
    public double spent;

    public Budget() {
    }

    public Budget(String budgetId, String category, double amount, double spent) {
        this.budgetId = budgetId;
        this.category = category;
        this.amount = amount;
        this.spent = spent;
    }

    public String getBudgetId() { return budgetId; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public double getSpent() { return spent; }
    
    public void setSpent(double spent) { this.spent = spent; }
}
