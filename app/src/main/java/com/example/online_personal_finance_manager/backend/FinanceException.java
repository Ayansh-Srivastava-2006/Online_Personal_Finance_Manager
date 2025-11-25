package com.example.online_personal_finance_manager.backend;

public class FinanceException extends Exception {
    public FinanceException(String message) {
        super(message);
    }

    public FinanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
