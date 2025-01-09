package com.minh.payday.ui.groups;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GroupsViewModel extends AndroidViewModel {
    private final GroupRepository groupRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<List<Group>> userGroupsLiveData = new MutableLiveData<>();
    private LiveData<Group> groupByIdLiveData;
    private final Application application;

    // For caching the user's groups
    public GroupsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
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

        // Fetch all groups where the user is a member
        db.collection("groups")
                .whereArrayContains("members", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Group> memberGroups = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            if (group != null) {
                                group.setGroupId(document.getId());
                                memberGroups.add(group);
                            }
                        }

                        // Fetch groups where the user is the owner
                        db.collection("groups")
                                .whereEqualTo("ownerId", userId)
                                .get()
                                .addOnCompleteListener(ownerTask -> {
                                    if (ownerTask.isSuccessful()) {
                                        List<Group> ownedGroups = new ArrayList<>();
                                        for (DocumentSnapshot document : ownerTask.getResult()) {
                                            Group group = document.toObject(Group.class);
                                            if (group != null && !containsGroup(memberGroups, group.getGroupId())) {
                                                group.setGroupId(document.getId());
                                                ownedGroups.add(group);
                                            }
                                        }

                                        // Combine both lists
                                        List<Group> allGroups = new ArrayList<>();
                                        allGroups.addAll(memberGroups);
                                        allGroups.addAll(ownedGroups);

                                        userGroupsLiveData.setValue(allGroups);
                                        Log.d("GroupsViewModel", "Fetching groups for user: " + userId);
                                        Log.d("GroupsViewModel", "Total groups fetched: " + allGroups.size());
                                    } else {
                                        Log.d("GroupsViewModel", "Error getting documents: ", ownerTask.getException());
                                        statusMessage.setValue("Error fetching groups: " + ownerTask.getException().getMessage());
                                    }
                                });
                    } else {
                        Log.d("GroupsViewModel", "Error getting documents: ", task.getException());
                        statusMessage.setValue("Error fetching groups: " + task.getException().getMessage());
                    }
                });
    }

    // Helper method to check if a group is already in the list
    private boolean containsGroup(List<Group> groups, String groupId) {
        for (Group group : groups) {
            if (group.getGroupId().equals(groupId)) {
                return true;
            }
        }
        return false;
    }

    public void createOfflineGroup(Group group) {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            group.setGroupId(UUID.randomUUID().toString());
            group.setTemp(true);
            group.setOwnerId(currentUserId);
            saveGroupToFirestore(group);
        } else {
            statusMessage.setValue("User not signed in.");
        }
    }

    // Saves the group to Firestore
    private void saveGroupToFirestore(Group group) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups").document(group.getGroupId())
                .set(group.toMap())
                .addOnSuccessListener(aVoid -> {
                    statusMessage.setValue("Group created successfully!");
                    fetchGroupsForUser(getCurrentUserId()); // Refresh the list after saving
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupsViewModel", "Error saving group: " + e.getMessage());
                    statusMessage.setValue("Error saving group: " + e.getMessage());
                });
    }

    public String getCurrentUserId() {
        // Assuming you are using Firebase Authentication
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    public void createOrUpdateGroup(Group group) {
        // Always set isTemp to false for online groups
        group.setTemp(false);

        if (group.getGroupId() == null || group.getGroupId().isEmpty()) {
            // Create a new group with a new UUID
            group.setGroupId(UUID.randomUUID().toString());
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