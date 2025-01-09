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
import java.util.List;
import java.util.UUID;

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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = currentUser.getUid();

        // Create a new Group object for an online group
        Group group = new Group();
        group.setGroupId(UUID.randomUUID().toString()); // Generate a unique ID for the online group
        group.setGroupName(groupName);
        group.setDescription(groupDescription);
        group.setOwnerId(currentUserId);
//        group.setOnline(true); // Mark as online
        group.setTemp(false); // Mark as not temp

        // Add the current user as the first member
        List<String> members = new ArrayList<>();
        members.add(currentUserId);
        group.setMembers(members);

        // Create the group in Firestore using the GroupsViewModel
        groupsViewModel.createOrUpdateGroup(group);

        // Observe the status message LiveData for updates from the ViewModel
        groupsViewModel.getStatusMessage().observe(this, message -> {
            if (message != null && message.contains("success")) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if group creation was successful
            }
        });
    }
}