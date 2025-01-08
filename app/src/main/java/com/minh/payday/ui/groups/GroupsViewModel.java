package com.minh.payday.ui.groups;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsViewModel extends ViewModel {
    private final GroupRepository groupRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Group>> userGroupsLiveData = new MutableLiveData<>();
    private LiveData<Group> groupByIdLiveData;

    // For caching the user's groups
    public GroupsViewModel() {
        groupRepository = new GroupRepository();
    }

    public LiveData<List<Group>> getUserGroupsLiveData() {
        return userGroupsLiveData;
    }
    public LiveData<Group> getGroupById(String groupId) {
        if (groupByIdLiveData == null) {
            groupByIdLiveData = groupRepository.getGroupById(groupId);
        }
        return groupByIdLiveData;
    }

    public void addUserToGroup(String groupCode, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming groupCode is the groupID for simplicity
        DocumentReference groupRef = db.collection("groups").document(groupCode);

        groupRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Group exists, add user to members
                    groupRef.update("members", FieldValue.arrayUnion(userId))
                            .addOnSuccessListener(aVoid -> statusMessage.setValue("User added to group successfully!"))
                            .addOnFailureListener(e -> statusMessage.setValue("Error adding user to group: " + e.getMessage()));
                } else {
                    statusMessage.setValue("Group not found.");
                }
            } else {
                statusMessage.setValue("Error checking group: " + task.getException().getMessage());
            }
        });
    }




    public void fetchGroupsForUser(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups")
                .whereArrayContains("members", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Group> groups = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            if (group != null) {
                                group.setGroupId(document.getId());
                                groups.add(group);
                            }
                        }
                        userGroupsLiveData.setValue(groups);
                    } else {
                        Log.d("GroupsViewModel", "Error getting documents: ", task.getException());
                        statusMessage.setValue("Error fetching groups: " + task.getException().getMessage());
                    }
                });
    }



    // Method to create an offline group
    public void createOfflineGroup(Group group) {
        // Implement your logic to save the group locally
        // For example, using SharedPreferences:
        saveGroupLocally(group);
    }

    private void saveGroupLocally(Group group) {
        // Example using SharedPreferences
        // Note: This is a simplified example. You might want to use a more robust solution like a local database.
    }

    public String getCurrentUserId() {
        // Assuming you are using Firebase Authentication
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    public void createOrUpdateGroup(Group group) {
        if (group.getGroupId() == null || group.getGroupId().isEmpty()) {
            // Create a new group
            createNewGroup(group);
        } else {
            // Update an existing group
            updateExistingGroup(group);
        }
    }

    private void createNewGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups")
                .add(group.toMap())
                .addOnSuccessListener(documentReference -> {
                    // Update the group with the generated ID
                    String generatedGroupId = documentReference.getId();
                    group.setGroupId(generatedGroupId);
                    updateExistingGroup(group);
                })
                .addOnFailureListener(e -> statusMessage.setValue("Error creating group: " + e.getMessage()));
    }

    private void updateExistingGroup(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups")
                .document(group.getGroupId())
                .set(group.toMap(), SetOptions.merge())
                .addOnSuccessListener(aVoid -> statusMessage.setValue("Group saved successfully!"))
                .addOnFailureListener(e -> statusMessage.setValue("Error saving group: " + e.getMessage()));
    }

    public MutableLiveData<String> getStatusMessage() {
        return statusMessage;
    }
}