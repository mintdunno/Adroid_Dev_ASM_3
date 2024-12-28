package com.minh.payday.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.ui.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Optional splash layout

        // Check if user is already logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Already logged in, go to MainActivity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // Not logged in, go to LoginActivity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish(); // Close SplashActivity
    }
}
