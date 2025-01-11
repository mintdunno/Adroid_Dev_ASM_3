package com.minh.payday.ui.profile;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.minh.payday.R;

import java.util.HashMap;
import java.util.Map;

public class PhoneNumberManager {

    private static final String TAG = "PhoneNumberManager";
    private final DatabaseReference userRef;
    private final Context context;

    public PhoneNumberManager(DatabaseReference userRef, Context context) {
        this.userRef = userRef;
        this.context = context;
    }

    public void fetchPhoneNumber(TextView phoneNumberTextView) {
        if (userRef == null) {
            Log.e(TAG, "Database reference is null!");
            return;
        }

        userRef.child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String phoneNumber = snapshot.getValue(String.class);
                    phoneNumberTextView.setText(phoneNumber != null ? phoneNumber : "Phone number is not provided");
                } else {
                    phoneNumberTextView.setText("Phone number is not provided");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch phone number: " + error.getMessage());
                Toast.makeText(context, "Error fetching phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showEditPhoneNumberDialog(TextView phoneNumberTextView) {
        if (context == null) return;

        // Create a dialog to edit the phone number
        EditText phoneNumberEditText = new EditText(context);
        phoneNumberEditText.setHint("Enter new phone number");

        new AlertDialog.Builder(context)
                .setTitle("Edit Phone Number")
                .setView(phoneNumberEditText)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newPhoneNumber = phoneNumberEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(newPhoneNumber)) {
                        savePhoneNumber(newPhoneNumber, phoneNumberTextView);
                    } else {
                        Toast.makeText(context, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void savePhoneNumber(String phoneNumber, TextView phoneNumberTextView) {
        if (userRef == null) {
            Log.e(TAG, "Database reference is null!");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("phoneNumber", phoneNumber);

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                phoneNumberTextView.setText(phoneNumber);
                Toast.makeText(context, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to update phone number: " + task.getException());
                Toast.makeText(context, "Failed to update phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
