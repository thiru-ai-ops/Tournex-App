package com.tournex.travel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private MaterialButton btnSignUp, btnGoogleSignUp;
    private Button btnLogin;
    private ImageView ivBack;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_GOOGLE_SIGN_UP = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        sharedPreferences = getSharedPreferences("TourNex", MODE_PRIVATE);

        // Configure Google Sign In - this enables account selection
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        ivBack = findViewById(R.id.ivBack);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnSignUp.setOnClickListener(v -> validateAndSignUp());

        btnGoogleSignUp.setOnClickListener(v -> {
            // This will show the Google account selection dialog
            progressDialog.setMessage("Opening Google account picker...");
            progressDialog.show();
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_UP);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void validateAndSignUp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Reset errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Full name is required");
            isValid = false;
        } else if (name.length() < 3) {
            tilName.setError("Name must be at least 3 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (isValid) {
            createUserWithEmailPassword(name, email, password);
        }
    }

    private void createUserWithEmailPassword(String name, String email, String password) {
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            // Update display name
                            com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                                    new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();
                            user.updateProfile(profileUpdates);

                            // Save user to Realtime Database
                            saveUserToDatabase(user.getUid(), name, email, "email");

                            // Save session
                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                            sharedPreferences.edit().putString("username", name).apply();
                            sharedPreferences.edit().putString("userEmail", email).apply();

                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                            // Navigate to MainActivity
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressDialog.dismiss();
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sign up failed";
                        Toast.makeText(SignUpActivity.this, "Sign up failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_UP) {
            // This is called AFTER user selects an account from the dialog
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // User has selected an account successfully
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Account was selected, now authenticate with Firebase
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // User cancelled account selection or there was an error
                progressDialog.dismiss();
                Toast.makeText(this, "Google sign up cancelled or failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        progressDialog.setMessage("Creating account with Google...");
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String name = user.getDisplayName() != null ? user.getDisplayName() : "";
                            String email = user.getEmail() != null ? user.getEmail() : "";

                            // Check if user already exists in database
                            databaseReference.child(user.getUid()).get().addOnCompleteListener(dbTask -> {
                                progressDialog.dismiss();

                                if (dbTask.isSuccessful() && dbTask.getResult() != null && dbTask.getResult().exists()) {
                                    // User already exists - just log in
                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                                    sharedPreferences.edit().putString("username", name).apply();
                                    sharedPreferences.edit().putString("userEmail", email).apply();

                                    Toast.makeText(SignUpActivity.this, "Welcome back " + name + "!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // New user - save to database
                                    saveUserToDatabase(user.getUid(), name, email, "google");

                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                                    sharedPreferences.edit().putString("username", name).apply();
                                    sharedPreferences.edit().putString("userEmail", email).apply();

                                    Toast.makeText(SignUpActivity.this, "Account created with Google! Welcome " + name + "!", Toast.LENGTH_SHORT).show();
                                }

                                // Navigate to MainActivity
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Google authentication failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String name, String email, String loginMethod) {
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("userId", userId);
        userMap.put("loginMethod", loginMethod);
        userMap.put("joinedDate", String.valueOf(System.currentTimeMillis()));

        databaseReference.child(userId).setValue(userMap)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}