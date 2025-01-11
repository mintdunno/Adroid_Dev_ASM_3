package com.minh.payday.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.payday.R;

public class EditPasswordDialog {

    private static final String TAG = "EditPasswordDialog";

    public static void show(@NonNull Context context) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_password, null);

        EditText oldPasswordEditText = dialogView.findViewById(R.id.oldPassword);
        EditText newPasswordEditText = dialogView.findViewById(R.id.newPassword);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPassword);

        // Build the dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Password")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String oldPassword = oldPasswordEditText.getText().toString().trim();
                    String newPassword = newPasswordEditText.getText().toString().trim();
                    String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                    // Validate inputs
                    if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(context, "New passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update the password
                    updatePassword(context, oldPassword, newPassword);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private static void updatePassword(Context context, String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(context, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(user.getEmail(), oldPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update the password
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, "Password updated successfully.");
                                    } else {
                                        Log.e(TAG, "Failed to update password: " + updateTask.getException());
                                        Toast.makeText(context, "Failed to update password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e(TAG, "Re-authentication failed: " + task.getException());
                        Toast.makeText(context, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
