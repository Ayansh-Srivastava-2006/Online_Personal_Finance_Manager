package com.example.online_personal_finance_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.online_personal_finance_manager.backend.User;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageButton btnMenu = findViewById(R.id.btnMenu);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); // Keep original icon colors if any

        // Set User Email in Header
        User user = FinanceManager.getInstance().getCurrentUser();
        if (user != null) {
            TextView tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);
            tvUserEmail.setText(user.getEmail());
        }

        // Menu Button Toggle
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // If not handled, the default back press action will be taken
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            selectedFragment = new DashboardFragment();
        } else if (id == R.id.nav_accounts) {
            selectedFragment = new AccountsFragment();
        } else if (id == R.id.nav_transactions) {
            selectedFragment = new TransactionsFragment();
        } else if (id == R.id.nav_budgets) {
            selectedFragment = new BudgetsFragment();
        } else if (id == R.id.nav_reports) {
            selectedFragment = new ReportsFragment();
        } else if (id == R.id.nav_logout) {
            FinanceManager.getInstance().logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
