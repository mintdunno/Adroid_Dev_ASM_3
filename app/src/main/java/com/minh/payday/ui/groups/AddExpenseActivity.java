package com.minh.payday.ui.groups;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.minh.payday.R;
import com.minh.payday.data.models.Expense;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.ExpenseRepository;
import com.minh.payday.data.repository.GroupRepository;
import com.minh.payday.ui.groups.adapters.MemberSplitAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private static final String TAG = "AddExpenseActivity";
    public static final String EXTRA_GROUP_ID = "extra_group_id";

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
        memberSplitAdapter = new MemberSplitAdapter(new ArrayList<>());
        membersRecyclerView.setAdapter(memberSplitAdapter);

        // Fetch group members from Firestore using GroupRepository
        groupRepository.getGroupById(groupId).observe(this, group -> {
            if (group != null) {
                setupPaidBySpinner(group.getMembers());
                setupMembersRecyclerView(group.getMembers());
            } else {
                Toast.makeText(this, "Error fetching group details", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        addExpenseButton.setOnClickListener(v -> addExpense());
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

    private void setupPaidBySpinner(List<String> members) {
        if (members != null && !members.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, members);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            paidBySpinner.setAdapter(adapter);
        } else {
            Log.e(TAG, "Members list is null or empty");
        }
    }

    private void setupMembersRecyclerView(List<String> members) {
        if (members != null && !members.isEmpty()) {
            memberSplitAdapter = new MemberSplitAdapter(members);
            membersRecyclerView.setAdapter(memberSplitAdapter);
        } else {
            Log.e(TAG, "Members list for RecyclerView is null or empty");
        }
    }

    private void addExpense() {
        String title = titleEditText.getText().toString();
        double amount = Double.parseDouble(amountEditText.getText().toString());
        String paidBy = paidBySpinner.getSelectedItem().toString();
        long timestamp = selectedDate.getTimeInMillis();

        // Assuming you have a way to get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a new Expense object
        Expense expense = new Expense();
        expense.setGroupId(groupId);
        expense.setAmount(amount);
        expense.setDescription(title);
        expense.setPayerId(paidBy);
        expense.setTimestamp(timestamp);

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
}