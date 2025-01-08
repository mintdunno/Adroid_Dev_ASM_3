package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.minh.payday.R;

public class JoinGroupActivity extends AppCompatActivity {

    private EditText groupCodeEditText;
    private Button joinGroupButton;
    private GroupsViewModel groupsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);

        groupCodeEditText = findViewById(R.id.groupCodeEditText);
        joinGroupButton = findViewById(R.id.joinGroupButton);

        joinGroupButton.setOnClickListener(v -> joinGroup());
    }

    private void joinGroup() {
        String groupCode = groupCodeEditText.getText().toString().trim();

        if (groupCode.isEmpty()) {
            groupCodeEditText.setError("Group code is required");
            return;
        }

        // Get the current user ID
        String currentUserId = groupsViewModel.getCurrentUserId();
        if (currentUserId == null) {
            Toast.makeText(this, "Error: User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the GroupsViewModel to add the user to the group
        groupsViewModel.addUserToGroup(groupCode, currentUserId);

        // Observe the status message LiveData for updates from the ViewModel
        groupsViewModel.getStatusMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                if (message.contains("success")) {
                    finish(); // Close the activity if joining the group was successful
                }
            }
        });
    }
}