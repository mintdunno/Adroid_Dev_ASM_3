package com.minh.payday.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.ui.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogout = findViewById(R.id.buttonLogout);

        // Logout
        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // Go back to Login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // ... other init, bottom nav, fragments, etc. ...
    }
}
