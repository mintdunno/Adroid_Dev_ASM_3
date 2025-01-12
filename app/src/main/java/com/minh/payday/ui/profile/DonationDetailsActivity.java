package com.minh.payday.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.minh.payday.R;
import com.minh.payday.ui.profile.ProfileFragment;

public class DonationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_details);

        EditText amountEditText = findViewById(R.id.amountEditText);
        Button notifySellerButton = findViewById(R.id.notifySellerButton);

        notifySellerButton.setOnClickListener(v -> {
            // Get the amount entered by the user
            String amount = amountEditText.getText().toString().trim();

            if (amount.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show a confirmation dialog
            showConfirmationDialog(amount);
        });
    }

    private void showConfirmationDialog(String amount) {
        new AlertDialog.Builder(this)
                .setTitle("Complete")
                .setMessage("Thank you. ₫" + amount + " sent.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Navigate back to the Profile page
                    Intent intent = new Intent(this, ProfileFragment.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .create()
                .show();
    }
}
