package com.example.online_personal_finance_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.online_personal_finance_manager.backend.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
            } else {
                FinanceManager.getInstance().login(username, password, new FinanceManager.CustomCallback<User>() {
                    @Override
                    public void onResult(User result) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, getString(R.string.welcome_back, result.getUsername()),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                getString(R.string.login_failed, e.getMessage()), Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
    }
}
