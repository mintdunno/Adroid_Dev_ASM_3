package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;

import java.util.ArrayList;
import java.util.List;

public class CreateOnlineGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private EditText groupDescriptionEditText;
    private Button createGroupButton;
    private GroupsViewModel groupsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_online_group);

        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);

        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText);
        createGroupButton = findViewById(R.id.createGroupButton);

        createGroupButton.setOnClickListener(v -> createGroup());
    }

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        String groupDescription = groupDescriptionEditText.getText().toString().trim();

        if (groupName.isEmpty()) {
            groupNameEditText.setError("Group name is required");
            return;
        }

        // Get the current user ID from FirebaseAuth
        String currentUserId = groupsViewModel.getCurrentUserId();
        if (currentUserId == null) {
            Toast.makeText(this, "Error: User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Group object
        Group group = new Group();
        group.setGroupName(groupName);
        group.setDescription(groupDescription);
        group.setOwnerId(currentUserId);
        group.setOnline(true);

        // Add the current user as the first member
        List<String> members = new ArrayList<>();
        members.add(currentUserId);
        group.setMembers(members);

        // Create the group in Firestore using the GroupsViewModel
        groupsViewModel.createOrUpdateGroup(group);

        // Observe the status message LiveData for updates from the ViewModel
        groupsViewModel.getStatusMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                if (message.contains("success")) {
                    finish(); // Close the activity if group creation was successful
                }
            }
        });
    }
}