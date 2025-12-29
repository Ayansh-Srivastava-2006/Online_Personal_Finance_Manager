package com.example.online_personal_finance_manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.online_personal_finance_manager.backend.User;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etFullName = findViewById(R.id.etFullName);
        final EditText etUsername = findViewById(R.id.etUsername);
        final EditText etEmail = findViewById(R.id.etEmail);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginHere = findViewById(R.id.tvLoginHere);
        TextView tvBackToHome = findViewById(R.id.tvBackToHome);

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString();
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
                return;
            }

            FinanceManager.getInstance().register(fullName, username, email, password, new FinanceManager.CustomCallback<User>() {
                @Override
                public void onResult(User result) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        String errorMessage = e.getMessage();
                        if (errorMessage == null || errorMessage.isEmpty()) {
                            errorMessage = "Registration failed. Please check your connection and try again.";
                        }
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("RegisterActivity", "Registration error", e);
                    });
                }
            });
        });

        tvLoginHere.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        tvBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
