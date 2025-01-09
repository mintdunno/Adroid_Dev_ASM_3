package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minh.payday.R;
import com.minh.payday.data.models.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CreateOfflineGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private EditText groupDescriptionEditText;
    private EditText memberNamesEditText;
    private Button createGroupButton;
    private GroupsViewModel groupsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offline_group);

        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);

        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText);
        memberNamesEditText = findViewById(R.id.memberNamesEditText);
        createGroupButton = findViewById(R.id.createGroupButton);

        createGroupButton.setOnClickListener(v -> createOfflineGroup());
    }

    private void createOfflineGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        String groupDescription = groupDescriptionEditText.getText().toString().trim();
        String memberNames = memberNamesEditText.getText().toString().trim();

        if (groupName.isEmpty()) {
            groupNameEditText.setError("Group name is required");
            return;
        }

        // Get the current user's ID
        String currentUserId = groupsViewModel.getCurrentUserId();
        if (currentUserId == null) {
            Toast.makeText(this, "Error: User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Group object for offline group
        Group group = new Group();
        group.setGroupName(groupName);
        group.setDescription(groupDescription);
        group.setTemp(true); // set isTemp to true for temp group
        group.setOwnerId(currentUserId); // Set the owner ID

        // Convert comma-separated member names to a list
        List<String> members = new ArrayList<>();
        if (!memberNames.isEmpty()) {
            String[] memberNameArray = memberNames.split(",");
            for (String name : memberNameArray) {
                members.add(name.trim());
            }
        }

        group.setMembers(members);

        // Save the offline group using the GroupsViewModel
        groupsViewModel.createOfflineGroup(group);

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