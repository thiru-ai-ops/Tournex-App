package com.tournex.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize sharedPreferences first
        sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);
        // Apply theme after sharedPreferences is initialized
        applySavedTheme();
        super.onCreate(savedInstanceState);
    }

    protected void applySavedTheme() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);
        }
        int savedTheme = sharedPreferences.getInt("app_theme", 0); // Default to Light (0)
        switch (savedTheme) {
            case 0: // Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1: // Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2: // System Default
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
        }
    }

    protected void setupBottomNavigation(int selectedItemId) {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    if (!(this instanceof MainActivity)) {
                        startActivity(new Intent(this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    return true;
                } else if (id == R.id.nav_budget) {
                    if (!(this instanceof BudgetPlannerActivity)) {
                        startActivity(new Intent(this, BudgetPlannerActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    return true;
                } else if (id == R.id.nav_mood) {
                    if (!(this instanceof MoodActivity)) {
                        startActivity(new Intent(this, MoodActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    return true;
                } else if (id == R.id.nav_bookings) {
                    if (!(this instanceof BookingActivity)) {
                        startActivity(new Intent(this, BookingActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    return true;
                } else if (id == R.id.nav_profile) {
                    if (!(this instanceof ProfileActivity)) {
                        startActivity(new Intent(this, ProfileActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    return true;
                }
                return false;
            });
        }
    }
}