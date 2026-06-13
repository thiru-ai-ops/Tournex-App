package com.tournex.travel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity {

    private ImageView ivBack, ivProfileImage;
    private TextView tvUserName, tvUserEmail, tvMemberSince, tvThemeStatus;
    private CardView cardEditProfile, cardChangePassword, cardNotifications, cardHelp, cardAbout, cardAppearance;
    private MaterialButton btnLogout;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupBottomNavigation(R.id.nav_profile);

        sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();

        initViews();
        loadUserData();
        setupClickListeners();
        updateThemeStatus();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        tvThemeStatus = findViewById(R.id.tvThemeStatus);
        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardChangePassword = findViewById(R.id.cardChangePassword);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardHelp = findViewById(R.id.cardHelp);
        cardAbout = findViewById(R.id.cardAbout);
        cardAppearance = findViewById(R.id.cardAppearance);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        String username = sharedPreferences.getString("username", "Traveler");
        String userEmail = sharedPreferences.getString("userEmail", "user@tournex.com");

        tvUserName.setText(username);
        tvUserEmail.setText(userEmail);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && user.getMetadata() != null) {
            long creationTimestamp = user.getMetadata().getCreationTimestamp();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault());
            String memberSince = sdf.format(new java.util.Date(creationTimestamp));
            tvMemberSince.setText("Member since " + memberSince);
        } else {
            tvMemberSince.setText("Member since 2024");
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        cardEditProfile.setOnClickListener(v -> showEditProfileDialog());

        cardChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change Password - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        cardNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        cardAppearance.setOnClickListener(v -> showAppearanceDialog());

        cardHelp.setOnClickListener(v -> showHelpDialog());

        cardAbout.setOnClickListener(v -> showAboutDialog());

        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void showAppearanceDialog() {
        String[] themes = {"Light Mode", "Dark Mode", "System Default"};
        int currentTheme = sharedPreferences.getInt("app_theme", 0);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Choose Theme");
        builder.setSingleChoiceItems(themes, currentTheme, (dialog, which) -> {
            applyTheme(which);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void applyTheme(int themeIndex) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("app_theme", themeIndex);
        editor.apply();

        switch (themeIndex) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "Light Mode applied", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "Dark Mode applied", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                Toast.makeText(this, "Following System Theme", Toast.LENGTH_SHORT).show();
                break;
        }

        updateThemeStatus();
        recreate();
    }

    private void updateThemeStatus() {
        if (tvThemeStatus != null) {
            int currentTheme = sharedPreferences.getInt("app_theme", 0);
            switch (currentTheme) {
                case 0:
                    tvThemeStatus.setText("Light Mode");
                    break;
                case 1:
                    tvThemeStatus.setText("Dark Mode");
                    break;
                case 2:
                    tvThemeStatus.setText("System Default");
                    break;
            }
        }
    }

    private void showHelpDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Help & Support");
        builder.setMessage("For any assistance, please contact us at:\n\nsupport@tournex.com\n\nCall: +91 98765 43210");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("About TourNex");
        builder.setMessage("TourNex Version 1.0\n\nYour ultimate Tamil Nadu travel companion. Plan trips, discover places by mood, manage budget, and book exciting tours.\n\n© 2024 TourNex. All rights reserved.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showEditProfileDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etEditName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etEditPhone);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveProfile);

        etName.setText(tvUserName.getText().toString());

        builder.setView(dialogView);
        builder.setTitle("Edit Profile");
        builder.setCancelable(true);
        androidx.appcompat.app.AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (!newName.isEmpty()) {
                sharedPreferences.edit().putString("username", newName).apply();
                tvUserName.setText(newName);

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                            new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(newName)
                                    .build();
                    user.updateProfile(profileUpdates);
                }

                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void logoutUser() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            sharedPreferences.edit().clear().apply();

            if (firebaseAuth != null) {
                firebaseAuth.signOut();
            }

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}