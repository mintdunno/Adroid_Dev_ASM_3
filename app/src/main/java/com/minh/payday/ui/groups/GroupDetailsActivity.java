package com.minh.payday.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.groups.MembersAdapter;

import java.util.ArrayList;

public class GroupDetailsActivity extends AppCompatActivity {

    private TextView groupNameTextView;
    private RecyclerView membersRecyclerView;
    private RecyclerView expensesRecyclerView;
    private FloatingActionButton addExpenseButton;
    private GroupsViewModel groupsViewModel;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);

        groupNameTextView = findViewById(R.id.groupNameTextView);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        addExpenseButton = findViewById(R.id.addExpenseButton);

        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            Toast.makeText(this, "Group ID is required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerViews();
        loadGroupDetails();

        addExpenseButton.setOnClickListener(v -> {
            // Navigate to AddExpenseActivity
            Intent intent = new Intent(this, AddExpenseActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    private void setupRecyclerViews() {
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // You'll need to create adapters for members and expenses
        MembersAdapter membersAdapter = new MembersAdapter(new ArrayList<>());
        membersRecyclerView.setAdapter(membersAdapter);

        // ExpensesAdapter expensesAdapter = new ExpensesAdapter(new ArrayList<>());
        // expensesRecyclerView.setAdapter(expensesAdapter);
    }

    private void loadGroupDetails() {
        groupsViewModel.getGroupById(groupId).observe(this, group -> {
            if (group != null) {
                groupNameTextView.setText(group.getGroupName());
                // Update the members adapter
                ((MembersAdapter) membersRecyclerView.getAdapter()).setMembers(group.getMembers());
            } else {
                Toast.makeText(this, "Error loading group details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}