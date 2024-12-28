package com.minh.payday.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.ui.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textWelcome;
    private Button buttonLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // <-- The layout "view" for MainActivity

        // Find views
        textWelcome = findViewById(R.id.textWelcomeMain);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Optionally, display the current user’s email or name
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            textWelcome.setText(getString(R.string.main_welcome_user, userEmail));
        }

        // Handle logout
        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // After signOut, go back to Login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
