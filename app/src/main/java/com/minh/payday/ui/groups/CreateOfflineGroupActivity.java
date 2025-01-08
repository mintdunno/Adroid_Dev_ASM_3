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
import java.util.Arrays;
import java.util.List;

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

        // Create a new Group object for offline group
        Group group = new Group();
        group.setGroupName(groupName);
        group.setDescription(groupDescription);
        group.setOnline(false);

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