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

        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

                if (expense.getPayerId() != null) {
                    String payerName = expense.getPayerId();
                    if (payerName.equals(expense.getOwnerId())) {
                        payerName += " (Me)";
                    }
                    payerNameTextView.setText(payerName);
                    payerAmountTextView.setText(String.format(Locale.getDefault(), "$%.2f", expense.getAmount()));

                    // No need to set background resource here
                    payerSubtitleTextView.setText("");
                }

                setupParticipantsRecyclerView(expense.getParticipants(), expense.getMemberAmounts(), currentUser);
            }
        });
    }

    private void setupParticipantsRecyclerView(List<String> participantIds, Map<String, Double> memberAmounts, FirebaseUser currentUser) {
        if (participantIds == null || participantIds.isEmpty()) {
            Log.e(TAG, "Participant IDs list is null or empty");
            return;
        }

        List<ParticipantItem> participantItems = new ArrayList<>();
        participantAdapter = new ParticipantAdapter(participantItems, currentUser.getUid());
        participantsRecyclerView.setAdapter(participantAdapter);

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
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.fetchUserById(currentUserId).observe(this, user -> {
            if (user != null) {
                // Append "(Me)" to the current user's name
                String displayName = user.getFirstName() + " (Me)";
                double amount = memberAmounts.getOrDefault(user.getUserId(), 0.00);
                // Use the modified display name for the current user
                participantItems.add(new ParticipantItem(user.getUserId(), displayName, amount));
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
            participantAdapter.updateParticipants(participantItems);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}