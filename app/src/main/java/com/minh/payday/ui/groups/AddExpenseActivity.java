package com.minh.payday.ui.groups;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.payday.R;
import com.minh.payday.data.models.Expense;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.ExpenseRepository;
import com.minh.payday.data.repository.GroupRepository;
import com.minh.payday.data.repository.UserRepository;
import com.minh.payday.ui.groups.adapters.MemberSplitAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    private static final String TAG = "AddExpenseActivity";
    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String OWNER_IDENTIFIER = "<<OWNER>>";


    private EditText titleEditText;
    private EditText amountEditText;
    private Spinner paidBySpinner;
    private TextView dateTextView;
    private TextView splitTypeTextView;
    private RecyclerView membersRecyclerView;
    private Button addExpenseButton;
    private MemberSplitAdapter memberSplitAdapter;
    private Calendar selectedDate = Calendar.getInstance();

    private GroupRepository groupRepository;
    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private AddExpenseViewModel viewModel;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        if (groupId == null) {
            Log.e(TAG, "No group ID provided to AddExpenseActivity");
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AddExpenseViewModel.class);
        groupRepository = new GroupRepository();
        expenseRepository = new ExpenseRepository();
        userRepository = new UserRepository();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleEditText = findViewById(R.id.titleEditText);
        amountEditText = findViewById(R.id.amountEditText);
        paidBySpinner = findViewById(R.id.paidBySpinner);
        dateTextView = findViewById(R.id.dateTextView);
        splitTypeTextView = findViewById(R.id.splitTypeTextView);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        addExpenseButton = findViewById(R.id.addExpenseButton);

        dateTextView.setOnClickListener(v -> showDatePicker());

        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberSplitAdapter = new MemberSplitAdapter(new ArrayList<>(), new ArrayList<>());
        membersRecyclerView.setAdapter(memberSplitAdapter);

        // Fetch group members from Firestore using GroupRepository
        groupRepository.getGroupById(groupId).observe(this, group -> {
            if (group != null) {
                fetchUserDataForMembers(group.getMembers(), group.getOwnerId());
            } else {
                Toast.makeText(this, "Error fetching group details", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        addExpenseButton.setOnClickListener(v -> addExpense());

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSplitAmounts();
            }
        });

        // Add OnKeyListener to amountEditText
        amountEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Update the split amounts in real-time
                updateSplitAmounts();
                return true;
            }
            return false;
        });
    }

    public void updateSplitAmounts() {
        String amountString = amountEditText.getText().toString().trim();
        List<String> selectedMembers = memberSplitAdapter.getSelectedMembers();
        if (!amountString.isEmpty() && !selectedMembers.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountString);
                double splitAmount = amount / selectedMembers.size();

                for (int i = 0; i < memberSplitAdapter.getItemCount(); i++) {
                    memberSplitAdapter.updateMemberAmount(i, selectedMembers.contains(memberSplitAdapter.getMemberId(i)) ? splitAmount : 0.00);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid amount format", e);
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            }
        } else {
            for (int i = 0; i < memberSplitAdapter.getItemCount(); i++) {
                memberSplitAdapter.updateMemberAmount(i, 0.00);
            }
        }
    }

    private void fetchUserDataForMembers(List<String> memberIds, String ownerId) {
        List<String> memberNames = new ArrayList<>();
        List<String> selectedMemberIds = new ArrayList<>(); // List to track selected member IDs

        for (String memberId : memberIds) {
            if (isUserId(memberId)) {
                userRepository.fetchUserById(memberId).observe(this, user -> {
                    if (user != null) {
                        String memberName = user.getFirstName();
                        if (memberId.equals(ownerId)) {
                            memberName += " (Me)";
                        }
                        memberNames.add(memberName);
                        selectedMemberIds.add(memberId);
                    } else {
                        memberNames.add("Unknown User");
                    }
                    if (memberNames.size() == memberIds.size()) {
                        setupPaidBySpinner(memberNames);
                        setupMembersRecyclerView(memberIds, memberNames, selectedMemberIds);
                    }
                });
            } else {
                memberNames.add(memberId);
                if (memberNames.size() == memberIds.size()) {
                    setupPaidBySpinner(memberNames);
                    setupMembersRecyclerView(memberIds, memberNames, selectedMemberIds);
                }
            }
        }
    }

    private boolean isUserId(String id) {
        return id.length() == 28;
    }

    private void showDatePicker() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year1);
                    selectedDate.set(Calendar.MONTH, monthOfYear);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                }, year, month, day);

        datePickerDialog.show();
    }

    private void updateDateLabel() {
        String myFormat = "MM/dd/yy"; // Choose your desired format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dateTextView.setText(sdf.format(selectedDate.getTime()));
    }

    private void setupPaidBySpinner(List<String> memberNames) {
        if (memberNames != null && !memberNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, memberNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            paidBySpinner.setAdapter(adapter);
        } else {
            Log.e(TAG, "Members list is null or empty");
        }
    }

    private void setupMembersRecyclerView(List<String> memberIds, List<String> memberNames, List<String> selectedMemberIds) {
        memberSplitAdapter = new MemberSplitAdapter(memberIds, memberNames);
        membersRecyclerView.setAdapter(memberSplitAdapter);
        memberSplitAdapter.selectAllMembers();
        updateSplitAmounts();
    }

    private void addExpense() {
        String title = titleEditText.getText().toString();
        String amountString = amountEditText.getText().toString();
        // Get the currently selected item in the spinner
        String paidBy = paidBySpinner.getSelectedItem() != null ? paidBySpinner.getSelectedItem().toString() : "";
        // Remove the "(Me)" if it's appended to the payer's name
        paidBy = paidBy.replace(" (Me)", "");
        long timestamp = selectedDate.getTimeInMillis();
        List<String> selectedMemberIds = memberSplitAdapter.getSelectedMemberIds();

        // Basic input validation
        if (title.isEmpty() || amountString.isEmpty() || paidBy.isEmpty() || selectedMemberIds.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate split amount
        Map<String, Double> memberAmounts = calculateSplit(amount, selectedMemberIds.size());

        // Create a new Expense object
        Expense expense = new Expense();
        expense.setGroupId(groupId);
        expense.setAmount(amount);
        expense.setDescription(title);
        expense.setTimestamp(timestamp);
        expense.setParticipants(selectedMemberIds);
        expense.setMemberAmounts(memberAmounts);

        // Set the ownerId to the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            expense.setOwnerId(currentUser.getUid());
        } else {
            // Handle the case where the user is not logged in (maybe set ownerId to paidBy for Quick Groups)
            expense.setOwnerId(paidBy);
        }

        // Assuming 'paidBy' is the name selected in the spinner, which includes "(Me)" for the owner
        // Now set the payerId, removing "(Me)" if it's appended
        expense.setPayerId(paidBy);

        // Add the expense to Firestore
        expenseRepository.addOrUpdateExpense(expense)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddExpenseActivity.this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding expense", e);
                    Toast.makeText(AddExpenseActivity.this, "Failed to add expense", Toast.LENGTH_SHORT).show();
                });
    }

    private Map<String, Double> calculateSplit(double totalAmount, int numMembers) {
        Map<String, Double> memberAmounts = new HashMap<>();
        if (numMembers > 0) {
            double splitAmount = totalAmount / numMembers;
            String ownerName = paidBySpinner.getSelectedItem().toString().replace(" (Me)", "");
            for (int i = 0; i < numMembers; i++) {
                String memberName = memberSplitAdapter.getMemberName(i);
                if (memberName != null) {
                    // If memberName is the owner, add a special identifier
                    if (memberName.equals(ownerName)) {
                        memberAmounts.put("<<OWNER>>", splitAmount);
                    } else {
                        memberAmounts.put(memberName, splitAmount);
                    }
                } else {
                    Log.e(TAG, "Member name is null at index: " + i);
                }
            }
        }
        return memberAmounts;
    }
}