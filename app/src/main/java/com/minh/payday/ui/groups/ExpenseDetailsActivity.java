package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.payday.R;
import com.minh.payday.data.models.Expense;
import com.minh.payday.data.models.User;
import com.minh.payday.data.repository.ExpenseRepository;
import com.minh.payday.data.repository.UserRepository;
import com.minh.payday.ui.groups.adapters.ParticipantAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    private static final String TAG = "ExpenseDetailsActivity";

    private TextView expenseTitleTextView, expenseDateTextView;
    private ImageView expenseImageView, payerAvatarImageView;
    private TextView payerNameTextView, payerSubtitleTextView, payerAmountTextView;
    private RecyclerView participantsRecyclerView;
    private LinearLayout payerInfoLayout;
    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private ParticipantAdapter participantAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        expenseTitleTextView = findViewById(R.id.expenseTitleTextView);
        expenseDateTextView = findViewById(R.id.expenseDateTextView);
        expenseImageView = findViewById(R.id.expenseImageView);
        payerAvatarImageView = findViewById(R.id.payerAvatarImageView);
        payerNameTextView = findViewById(R.id.payerNameTextView);
        payerSubtitleTextView = findViewById(R.id.payerSubtitleTextView);
        payerAmountTextView = findViewById(R.id.payerAmountTextView);
        participantsRecyclerView = findViewById(R.id.participantsRecyclerView);
        payerInfoLayout = findViewById(R.id.payerInfoLayout);

        expenseRepository = new ExpenseRepository();
        userRepository = new UserRepository();

        // Set up RecyclerView and Adapter
        participantAdapter = new ParticipantAdapter(new ArrayList<>(), ""); // Initialize with empty list
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        participantsRecyclerView.setAdapter(participantAdapter);

        String expenseId = getIntent().getStringExtra(EXTRA_EXPENSE_ID);
        if (expenseId != null) {
            fetchExpenseDetails(expenseId);
        } else {
            Log.e(TAG, "No expense ID provided to ExpenseDetailsActivity");
            finish();
        }
    }

    private void fetchExpenseDetails(String expenseId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User not logged in");
            finish();
            return;
        }

        expenseRepository.getExpenseById(expenseId).observe(this, expense -> {
            if (expense != null) {
                expenseTitleTextView.setText(expense.getDescription());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(expense.getTimestamp()));
                expenseDateTextView.setText(formattedDate);

                // Assuming the payer is also a participant
                if (expense.getPayerId() != null) {
                    // Fetch user data only if the payer ID is a user ID, otherwise treat as a guest
                    if (isUserId(expense.getPayerId())) {
                        userRepository.fetchUserById(expense.getPayerId()).observe(this, payer -> {
                            if (payer != null) {
                                payerNameTextView.setText(payer.getFirstName());
                                if (payer.getUserId().equals(currentUser.getUid())) {
                                    payerSubtitleTextView.setText("Me");
                                    payerInfoLayout.setBackgroundResource(R.drawable.rounded_background);
                                } else {
                                    payerSubtitleTextView.setText("");
                                    payerInfoLayout.setBackgroundResource(R.drawable.rounded_background_light_orange);
                                }
                                payerAmountTextView.setText(String.format(Locale.getDefault(), "$%.2f", expense.getAmount()));
                            }
                        });
                    } else {
                        // Treat the payer as a guest
                        payerNameTextView.setText(expense.getPayerId());
                        payerSubtitleTextView.setText("Guest"); // Or any other identifier for non-registered users
                        payerInfoLayout.setBackgroundResource(R.drawable.rounded_background_light_orange);
                        payerAmountTextView.setText(String.format(Locale.getDefault(), "$%.2f", expense.getAmount()));
                    }
                }

                setupParticipantsRecyclerView(expense.getParticipants(), expense.getMemberAmounts(), currentUser);
            }
        });
    }

    // Method to check if an ID is a user ID
    private boolean isUserId(String id) {
        // Implement logic to check if the ID is a user ID or a guest identifier
        // For example, check the format or length of the ID
        return id.length() == 28; // Assuming user IDs are 28 characters long
    }

    private void setupParticipantsRecyclerView(List<String> participantIds, Map<String, Double> memberAmounts, FirebaseUser currentUser) {
        if (participantIds == null || participantIds.isEmpty()) {
            Log.e(TAG, "Participant IDs list is null or empty");
            return;
        }

        // Use a local list to collect participant items
        List<ParticipantItem> participantItems = new ArrayList<>();

        for (String participantId : participantIds) {
            // Determine if the participant is the current user or a guest
            if (isCurrentUser(participantId, currentUser)) {
                fetchAndAddCurrentUser(participantItems, memberAmounts, participantIds.size());
            } else {
                // Treat as guest participant
                addGuestParticipant(participantItems, participantId, memberAmounts, participantIds.size());
            }
        }
    }

    private boolean isCurrentUser(String participantId, FirebaseUser currentUser) {
        return currentUser != null && participantId.equals(currentUser.getUid());
    }

    private void fetchAndAddCurrentUser(List<ParticipantItem> participantItems, Map<String, Double> memberAmounts, int totalParticipants) {
        userRepository.fetchUserById(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(this, user -> {
            if (user != null) {
                double amount = memberAmounts.getOrDefault(user.getUserId(), 0.00);
                participantItems.add(new ParticipantItem(user, amount));
                checkAndUpdateAdapter(participantItems, totalParticipants);
            } else {
                Log.e(TAG, "Current user data is null");
            }
        });
    }

    private void addGuestParticipant(List<ParticipantItem> participantItems, String participantId, Map<String, Double> memberAmounts, int totalParticipants) {
        double amount = memberAmounts.getOrDefault(participantId, 0.00);
        participantItems.add(new ParticipantItem(participantId, amount));
        checkAndUpdateAdapter(participantItems, totalParticipants);
    }

    private void checkAndUpdateAdapter(List<ParticipantItem> participantItems, int totalParticipants) {
        if (participantItems.size() == totalParticipants) {
            // Update the adapter with the complete list
            participantAdapter.updateParticipants(participantItems);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}