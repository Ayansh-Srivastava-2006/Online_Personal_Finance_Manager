package com.example.online_personal_finance_manager.backend;

public class User {
    public String uid;
    public String fullName;
    public String username;
    public String email;
    public String password; // This will not be stored in the User object from DB

    public User(String uid, String fullName, String username, String email, String password) {
        this.uid = uid;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
