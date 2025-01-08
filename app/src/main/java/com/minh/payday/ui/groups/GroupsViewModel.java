package com.minh.payday.ui.groups;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

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
        // Fetch online groups
        db.collection("groups")
                .whereArrayContains("members", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Group> onlineGroups = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            if (group != null) {
                                group.setGroupId(document.getId());
                                group.setOnline(true); // Make sure to set isOnline to true for online groups
                                onlineGroups.add(group);
                            }
                        }

                        // Fetch offline groups
                        List<Group> offlineGroups = getOfflineGroups();

                        // Combine online and offline groups
                        List<Group> allGroups = new ArrayList<>();
                        allGroups.addAll(onlineGroups);
                        allGroups.addAll(offlineGroups);

                        userGroupsLiveData.setValue(allGroups); // Update LiveData
                        // ... logging statements ...
                    } else {
                        Log.d("GroupsViewModel", "Error getting documents: ", task.getException());
                        statusMessage.setValue("Error fetching groups: " + task.getException().getMessage());
                    }
                });
    }

    private List<Group> getOfflineGroups() {
        String userId = getCurrentUserId(); // Get the current user ID

        if (userId == null) {
            Log.e("GroupsViewModel", "Current user ID is null");
            return new ArrayList<>();
        }

        SharedPreferences prefs = application.getSharedPreferences("OfflineGroups", Context.MODE_PRIVATE);
        String offlineGroupsJson = prefs.getString(userId, null);
        if (offlineGroupsJson != null) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Group>>() {}.getType();
            return gson.fromJson(offlineGroupsJson, type);
        }
        return new ArrayList<>();
    }


    // Method to create an offline group
    public void createOfflineGroup(Group group) {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            group.setGroupId(UUID.randomUUID().toString());
            saveOfflineGroup(currentUserId, group);
            fetchGroupsForUser(currentUserId);
        } else {
            statusMessage.setValue("User not signed in.");
        }
    }

    private void saveOfflineGroup(String userId, Group group) {
        SharedPreferences prefs = application.getSharedPreferences("OfflineGroups", Context.MODE_PRIVATE);
        String offlineGroupsJson = prefs.getString(userId, null);
        List<Group> offlineGroups;

        Gson gson = new Gson();
        if (offlineGroupsJson == null) {
            offlineGroups = new ArrayList<>();
        } else {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Group>>() {}.getType();
            offlineGroups = gson.fromJson(offlineGroupsJson, type);
        }

        offlineGroups.add(group);
        String updatedOfflineGroupsJson = gson.toJson(offlineGroups);

        prefs.edit().putString(userId, updatedOfflineGroupsJson).apply();
        statusMessage.setValue("Offline group created successfully!");
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