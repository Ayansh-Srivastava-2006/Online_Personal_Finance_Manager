package com.example.finance.backend;

public class Budget {
    private String budgetId;
    private String category;
    private double amount;
    private double spent;

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

    public void setBudgetId(String budgetId) { this.budgetId = budgetId; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setSpent(double spent) { this.spent = spent; }
}


