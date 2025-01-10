package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.groups.adapters.ExpensesAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class LiveGroupDetailsActivity extends AppCompatActivity {

    private static final String TAG = "LiveGroupDetailsActivity";
    public static final String EXTRA_GROUP_ID = "groupId";

    private LiveGroupDetailsViewModel viewModel;
    private String groupId;

    private TextView groupNameTextView;
    private TextView groupDateTextView;
    private TextView groupLocationTextView;
    private TextView memberCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_group_details);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        if (groupId == null) {
            Log.e(TAG, "No group ID provided.");
            Toast.makeText(this, "Error: Group not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(LiveGroupDetailsViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        groupNameTextView = findViewById(R.id.groupNameTextView);
        groupDateTextView = findViewById(R.id.groupDateTextView);
        groupLocationTextView = findViewById(R.id.groupLocationTextView);
        memberCountTextView = findViewById(R.id.memberCountTextView);

        RecyclerView expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ExpensesAdapter expensesAdapter = new ExpensesAdapter(new ArrayList<>());
        expensesRecyclerView.setAdapter(expensesAdapter);

        // Observe LiveData and update UI
        viewModel.getGroupDetails(groupId).observe(this, group -> {
            if (group != null) {
                groupNameTextView.setText(group.getGroupName());
                // ... set other details like date, location, member count
            } else {
                // Handle error or no data case
                Toast.makeText(this, "Error loading group details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}