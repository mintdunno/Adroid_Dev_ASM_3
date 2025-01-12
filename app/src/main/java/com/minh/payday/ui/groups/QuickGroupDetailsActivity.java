package com.minh.payday.ui.groups;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.payday.R;
import com.minh.payday.data.models.Expense;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.models.User;
import com.minh.payday.data.repository.UserRepository;
import com.minh.payday.ui.MainActivity;
import com.minh.payday.ui.auth.LoginActivity;
import com.minh.payday.ui.groups.adapters.ExpensesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class QuickGroupDetailsActivity extends AppCompatActivity implements AddMemberDialogFragment.AddMemberDialogListener, ExpensesAdapter.OnItemClickListener {

    private static final String TAG = "QuickGroupDetailsActivity";
    public static final String EXTRA_GROUP_ID = "groupId";

    private QuickGroupDetailsViewModel viewModel;
    private String groupId;

    private TextView groupNameTextView;
    private TextView groupDateTextView;
    private TextView groupLocationTextView;
    private TextView memberCountTextView;
    private ExpensesAdapter expensesAdapter;
    private FloatingActionButton addExpenseFab;
    private UserRepository userRepository;

    private TextView myExpensesTextView;
    private TextView totalExpensesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_group_details);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        if (groupId == null) {
            Log.e(TAG, "No group ID provided.");
            Toast.makeText(this, "Error: Group not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize userRepository first
        userRepository = new UserRepository();

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupExpenseFab();

        // Now it's safe to call setupViewModel()
        setupViewModel();
    }

    private void initializeViews() {
        groupNameTextView = findViewById(R.id.groupNameTextView);
        groupDateTextView = findViewById(R.id.groupDateTextView);
        groupLocationTextView = findViewById(R.id.groupLocationTextView);
        memberCountTextView = findViewById(R.id.memberCountTextView);
        addExpenseFab = findViewById(R.id.addExpenseFab);
        myExpensesTextView = findViewById(R.id.myExpensesTextView);
        totalExpensesTextView = findViewById(R.id.totalExpensesTextView);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> showPopupMenu(v));
    }

    private void setupRecyclerView() {
        RecyclerView expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expensesAdapter = new ExpensesAdapter(new ArrayList<>(), this); // Now 'this' is valid
        expensesRecyclerView.setAdapter(expensesAdapter);
    }

    private void setupExpenseFab() {
        addExpenseFab.setOnClickListener(view -> {
            Intent intent = new Intent(QuickGroupDetailsActivity.this, AddExpenseActivity.class);
            intent.putExtra(AddExpenseActivity.EXTRA_GROUP_ID, groupId);
            // Use startActivityForResult or a similar mechanism to get a result back
            startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE);
        });
    }

    //add this two method in QuickGroupDetailsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Expense added successfully, refresh the data
            viewModel.refreshExpenses(groupId);
        }
    }
    public static final int ADD_EXPENSE_REQUEST_CODE = 1;
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(QuickGroupDetailsViewModel.class);
        viewModel.getGroupDetails(groupId).observe(this, this::updateUIWithGroupDetails);
        viewModel.getExpenses(groupId).observe(this, this::updateUIWithExpenses);
    }

    private void updateUIWithGroupDetails(Group group) {
        if (group != null) {
            groupNameTextView.setText(group.getGroupName());
            memberCountTextView.setText(String.valueOf(group.getMembers() != null ? group.getMembers().size() : 0));
        } else {
            Toast.makeText(this, "Error loading group details", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIWithExpenses(List<Expense> expenses) {
        if (expenses != null) {
            expensesAdapter.updateExpenses(expenses);
            calculateAndDisplayExpenses(expenses);
        } else {
            Toast.makeText(this, "Error loading expenses", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateAndDisplayExpenses(List<Expense> expenses) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            Log.e(TAG, "User not logged in");
            // Redirect to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close this activity
            return; // Stop further execution
        }

        // User is logged in, proceed with calculation
        userRepository.fetchUserById(currentUser.getUid()).observe(this, user -> {
            if (user != null) {
                String currentUserId = user.getUserId();
                double myTotalExpenses = 0;
                double totalExpenses = 0;

                for (Expense expense : expenses) {
                    totalExpenses += expense.getAmount();

                    // Check if the special owner identifier is present
                    if (expense.getMemberAmounts().containsKey("<<OWNER>>")) {
                        myTotalExpenses += expense.getMemberAmounts().get("<<OWNER>>");
                    } else if (expense.getMemberAmounts().containsKey(currentUserId)) {
                        // Check for current user ID in case of registered users
                        myTotalExpenses += expense.getMemberAmounts().get(currentUserId);
                    }
                }

                // Display the calculated amounts
                myExpensesTextView.setText(String.format(Locale.getDefault(), "$%.2f", myTotalExpenses));
                totalExpensesTextView.setText(String.format(Locale.getDefault(), "$%.2f", totalExpenses));
            } else {
                Log.e(TAG, "Could not fetch current user's ID");
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.group_settings_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_add_member) {
                showAddMemberDialog();
                return true;
            } else if (item.getItemId() == R.id.menu_delete_group) {
                showDeleteConfirmationDialog();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showAddMemberDialog() {
        AddMemberDialogFragment dialogFragment = new AddMemberDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "AddMemberDialogFragment");
    }

    @Override
    public void onMemberAdded(String memberName) {
        viewModel.addMemberToGroup(groupId, memberName);
        // Show success dialog
        new AlertDialog.Builder(this)
                .setTitle("Member Added")
                .setMessage(memberName + " has been added to the group.")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Group")
                .setMessage("Are you sure you want to delete this group?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGroup();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteGroup() {
        viewModel.deleteGroup(groupId).observe(this, success -> {
            if (success) {
                Toast.makeText(QuickGroupDetailsActivity.this, "Group deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuickGroupDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(QuickGroupDetailsActivity.this, "Error deleting group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Expense expense) {
        Intent intent = new Intent(this, ExpenseDetailsActivity.class);
        intent.putExtra(ExpenseDetailsActivity.EXTRA_EXPENSE_ID, expense.getExpenseId());
        startActivity(intent);
    }
}