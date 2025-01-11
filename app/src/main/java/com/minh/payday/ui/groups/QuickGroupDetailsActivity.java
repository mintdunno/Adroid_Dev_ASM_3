package com.minh.payday.ui.groups;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.MainActivity;
import com.minh.payday.ui.groups.adapters.ExpensesAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class QuickGroupDetailsActivity extends AppCompatActivity implements AddMemberDialogFragment.AddMemberDialogListener {

    private static final String TAG = "QuickGroupDetailsActivity";
    public static final String EXTRA_GROUP_ID = "groupId";

    private QuickGroupDetailsViewModel viewModel;
    private String groupId;

    private TextView groupNameTextView;
    private TextView groupDateTextView;
    private TextView groupLocationTextView;
    private TextView memberCountTextView;

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

        viewModel = new ViewModelProvider(this).get(QuickGroupDetailsViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show the back button

        groupNameTextView = findViewById(R.id.groupNameTextView);
        groupDateTextView = findViewById(R.id.groupDateTextView);
        groupLocationTextView = findViewById(R.id.groupLocationTextView);
        memberCountTextView = findViewById(R.id.memberCountTextView);

        RecyclerView expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ExpensesAdapter expensesAdapter = new ExpensesAdapter(new ArrayList<>());
        expensesRecyclerView.setAdapter(expensesAdapter);

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Observe LiveData and update UI
        viewModel.getGroupDetails(groupId).observe(this, group -> {
            if (group != null) {
                groupNameTextView.setText(group.getGroupName());
                groupDateTextView.setText("Your Date"); // Replace with actual date
                groupLocationTextView.setText("Your Location"); // Replace with actual location
                memberCountTextView.setText(String.valueOf(group.getMembers() != null ? group.getMembers().size() : 0));
            } else {
                // Handle error or no data case
                Toast.makeText(this, "Error loading group details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddMemberDialog() {
        AddMemberDialogFragment dialogFragment = new AddMemberDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "AddMemberDialogFragment");
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
    public void onMemberAdded(String memberName) {
        viewModel.addMemberToGroup(groupId, memberName);
        // Show success dialog
        new AlertDialog.Builder(this)
                .setTitle("Member Added")
                .setMessage(memberName + " has been added to the group.")
                .setPositiveButton(android.R.string.ok, null)
                .show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Handle back button press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}