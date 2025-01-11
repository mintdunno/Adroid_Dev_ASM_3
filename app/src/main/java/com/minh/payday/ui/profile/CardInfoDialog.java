package com.minh.payday.ui.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minh.payday.R;

import java.util.HashMap;
import java.util.Map;

public class CardInfoDialog {

    private static final String TAG = "CardInfoDialog";
    public interface CardInfoSaveListener {
        void onCardInfoSaved();
    }
    public static void show(@NonNull Context context, @NonNull DatabaseReference cardRef, @NonNull CardInfoSaveListener listener) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_card_info, null);

        EditText cardNumberEditText = dialogView.findViewById(R.id.editCardNumber);
        EditText expiryDateEditText = dialogView.findViewById(R.id.editExpiryDate);
        EditText ccvEditText = dialogView.findViewById(R.id.editCCV);
        EditText nameEditText = dialogView.findViewById(R.id.editName);

        // Build the dialog
        new AlertDialog.Builder(context)
                .setTitle("Enter Card Information")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String cardNumber = cardNumberEditText.getText().toString().trim();
                    String expiryDate = expiryDateEditText.getText().toString().trim();
                    String ccv = ccvEditText.getText().toString().trim();
                    String name = nameEditText.getText().toString().trim();

                    // Validate inputs
                    if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(expiryDate) || TextUtils.isEmpty(ccv) || TextUtils.isEmpty(name)) {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Save the card information to Firebase
                    saveCardInfo(cardRef, cardNumber, expiryDate, ccv, name, context, listener);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private static void saveCardInfo(DatabaseReference cardRef, String cardNumber, String expiryDate, String ccv, String name, Context context, CardInfoSaveListener listener) {
        if (cardRef == null) {
            Log.e(TAG, "Card reference is null!");
            Toast.makeText(context, "Unable to save card information", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cardInfo = new HashMap<>();
        cardInfo.put("cardNumber", cardNumber);
        cardInfo.put("expiryDate", expiryDate);
        cardInfo.put("ccv", ccv);
        cardInfo.put("name", name);

        cardRef.setValue(cardInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Card information saved successfully", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Card information saved.");

                if (listener != null) {
                    listener.onCardInfoSaved(); // Notify listener to update UI
                }
            } else {
                Toast.makeText(context, "Failed to save card information", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save card information: " + task.getException());
            }
        });
    }

}
