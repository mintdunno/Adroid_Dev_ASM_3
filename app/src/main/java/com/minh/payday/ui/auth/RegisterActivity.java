package com.minh.payday.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.minh.payday.R;
import com.minh.payday.data.models.User;
import com.minh.payday.ui.MainActivity;
import com.minh.payday.ui.user.UserViewModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextFirstName, editTextLastName, editTextEmail,
            editTextPassword, editTextConfirmPassword;
    private MaterialButton buttonRegister;

    private UserViewModel userViewModel;
    // Handle the up arrow
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // This mimics the user pressing back
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe status messages
        userViewModel.getStatusMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe current user for navigation
        userViewModel.getCurrentUserLiveData().observe(this, user -> {
            if (user != null) {
                // Registration complete, go to MainActivity
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        // Find views
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPasswordRegister);

        buttonRegister = findViewById(R.id.buttonRegister);

        // Handle register button click
        buttonRegister.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirm = editTextConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
                    || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tell the ViewModel to register
            userViewModel.registerUser(email, password, firstName, lastName);
        });

        // Link to Login
        findViewById(R.id.textLoginLink).setOnClickListener(v -> {
            finish(); // or startActivity(new Intent(this, LoginActivity.class));
        });
    }


}
