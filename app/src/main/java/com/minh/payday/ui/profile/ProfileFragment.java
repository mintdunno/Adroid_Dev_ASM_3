package com.minh.payday.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minh.payday.R;
import com.minh.payday.ui.auth.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private EditText firstNameEditText, lastNameEditText;
    private TextView emailTextView, editPasswordTextView, cardInfoTextView, phoneNumberTextView;
    private ImageView editCardInfoButton;
    private DatabaseReference userRef;
    private DatabaseReference cardRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        fetchCardInfo();

        // Initialize views
        firstNameEditText = view.findViewById(R.id.editFirstName);
        lastNameEditText = view.findViewById(R.id.editLastName);
        emailTextView = view.findViewById(R.id.email);
        editPasswordTextView = view.findViewById(R.id.editPassword);
        cardInfoTextView = view.findViewById(R.id.cardInfoTextView);
        editCardInfoButton = view.findViewById(R.id.editCardInfoButton);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        ImageView editPhoneNumberButton = view.findViewById(R.id.editPhoneNumberButton);

        Button changeCardButton = view.findViewById(R.id.changeCardButton);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        // Get Firebase User and Database Reference
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String userId = user.getUid();
            String email = user.getEmail();
            emailTextView.setText(email);

            // Reference to user's data in Firebase Database
            userRef = FirebaseDatabase.getInstance("https://payday-caf8e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users").child(userId);
            cardRef = FirebaseDatabase.getInstance("https://payday-caf8e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users").child(userId).child("cardInfo");

            // Fetch and display user data
            fetchUserData();
            fetchPhoneNumber();
            fetchCardInfo();
        } else {
            Log.e(TAG, "User not authenticated!");
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Save changes when focus is lost
        firstNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveUserInfo("firstName", firstNameEditText.getText().toString().trim());
            }
        });

        lastNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveUserInfo("lastName", lastNameEditText.getText().toString().trim());
            }
        });

        editPasswordTextView.setOnClickListener(v -> {
            if (getContext() != null) {
                EditPasswordDialog.show(getContext());
            }
        });

        editCardInfoButton.setOnClickListener(v -> {
            if (getContext() != null && cardRef != null) {
                CardInfoDialog.show(getContext(), cardRef, this::fetchCardInfo);
            }
        });

        editPhoneNumberButton.setOnClickListener(v -> {
            if (getContext() != null) {
                showEditPhoneNumberDialog();
            }
        });

        changeCardButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Change Card clicked!", Toast.LENGTH_SHORT).show();
        });

        // Log Out button click
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Sign out the user
            Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish(); // Close the current activity
            }
        });
        return view;
    }

    private void fetchUserData() {
        if (userRef == null) {
            Log.e(TAG, "Database reference is null!");
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Data snapshot: " + snapshot.toString());
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);

                    Log.d(TAG, "First Name: " + firstName);
                    Log.d(TAG, "Last Name: " + lastName);

                    if (firstName != null) firstNameEditText.setText(firstName);
                    if (lastName != null) lastNameEditText.setText(lastName);
                } else {
                    Log.w(TAG, "No data found for the user.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user data: " + error.getMessage());
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            if (getContext() != null) {
                Toast.makeText(getContext(), key + " cannot be empty", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (userRef == null) {
            Log.e(TAG, "Database reference is null!");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (getContext() == null) {
                Log.w(TAG, "Fragment is not attached to a context. Skipping toast.");
                return; // Exit early to prevent a crash
            }

            if (task.isSuccessful()) {
                Toast.makeText(getContext(), key + " updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update " + key, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCardInfo() {
        if (cardInfoTextView == null) {
            Log.e(TAG, "cardInfoTextView is null. Ensure it is initialized.");
            return;
        }

        if (cardRef == null) {
            Log.e(TAG, "Card reference is null!");
            cardInfoTextView.setText("No card information available");
            return;
        }

        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cardNumber = snapshot.child("cardNumber").getValue(String.class);
                    String expiryDate = snapshot.child("expiryDate").getValue(String.class);
                    String ccv = snapshot.child("ccv").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);

                    String cardInfo = "Card Number: " + (cardNumber != null ? cardNumber : "N/A") + "\n" +
                            "Expiry Date: " + (expiryDate != null ? expiryDate : "N/A") + "\n" +
                            "CCV: " + (ccv != null ? ccv : "N/A") + "\n" +
                            "Name: " + (name != null ? name : "N/A");

                    cardInfoTextView.setText(cardInfo);
                } else {
                    cardInfoTextView.setText("No card information available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch card info: " + error.getMessage());
                Toast.makeText(getContext(), "Error fetching card info", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchPhoneNumber() {
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
                Toast.makeText(getContext(), "Error fetching phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditPhoneNumberDialog() {
        if (getContext() == null) return;

        // Create a dialog to edit the phone number
        EditText phoneNumberEditText = new EditText(getContext());
        phoneNumberEditText.setHint("Enter new phone number");

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Phone Number")
                .setView(phoneNumberEditText)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newPhoneNumber = phoneNumberEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(newPhoneNumber)) {
                        savePhoneNumber(newPhoneNumber);
                    } else {
                        Toast.makeText(getContext(), "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void savePhoneNumber(String phoneNumber) {
        if (userRef == null) {
            Log.e(TAG, "Database reference is null!");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("phoneNumber", phoneNumber);

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                phoneNumberTextView.setText(phoneNumber);
                Toast.makeText(getContext(), "Phone number updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to update phone number: " + task.getException());
                Toast.makeText(getContext(), "Failed to update phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
