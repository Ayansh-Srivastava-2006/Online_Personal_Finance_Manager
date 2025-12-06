package com.example.online_personal_finance_manager.backend;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String uid;
    public String username;
    public String email;
    public String password; // For registration only, don't store long term

    public User() {
    }

    public User(String uid, String email, String password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
