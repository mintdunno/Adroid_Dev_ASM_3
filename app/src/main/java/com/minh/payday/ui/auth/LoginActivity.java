package com.minh.payday.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.ui.MainActivity;
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textForgotPassword;
    private TextView textRegisterLink;
    private TextInputLayout textInputLayoutPassword;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views by ID
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textForgotPassword = findViewById(R.id.textForgotPassword);
        textRegisterLink = findViewById(R.id.textRegisterLink);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        textInputLayoutPassword.setEndIconDrawable(R.drawable.ic_show);
        clearIconTint();

        textInputLayoutPassword.setEndIconOnClickListener(v -> togglePasswordVisibility());

        // Click: Login
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

//            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
//                Toast.makeText(LoginActivity.this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
//                return;
//            }
            loginUser(email, password);
        });

        // Click: Forgot Password
        textForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        // Click: Click Register
        textRegisterLink.setOnClickListener(v -> {
            // Go to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void clearIconTint() {
        // Clear the tint for the end icon
        textInputLayoutPassword.setEndIconTintList(null);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            textInputLayoutPassword.setEndIconDrawable(R.drawable.ic_show);
        } else {
            // Show password
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            textInputLayoutPassword.setEndIconDrawable(R.drawable.ic_hide);
        }
        clearIconTint();
        isPasswordVisible = !isPasswordVisible;

        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    private void loginUser(String email, String password) {

        // Temporarily bypass Firebase authentication for testing
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Bypassing login for testing purposes", Toast.LENGTH_SHORT).show();
            // Directly go to MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // close login screen
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // Go to MainActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // close login screen
                    } else {
                        String error = (task.getException() != null)
                                ? task.getException().getMessage()
                                : "Unknown error";
                        Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
