package com.minh.payday.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.minh.payday.data.models.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GroupRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;
    private static final String TAG = "GroupRepository";

    public GroupRepository() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<List<Group>> getLiveGroups(String userId) {
        MutableLiveData<List<Group>> liveGroups = new MutableLiveData<>();
        firestore.collection("groups")
                .whereEqualTo("groupType", Group.GroupType.LIVE)
                .whereArrayContains("members", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        liveGroups.setValue(null);
                        return;
                    }
                    List<Group> groups = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Group group = doc.toObject(Group.class);
                            if (group != null) {
                                group.setGroupId(doc.getId());
                                groups.add(group);
                            }
                        }
                    }
                    liveGroups.setValue(groups);
                });
        return liveGroups;
    }

    public LiveData<List<Group>> getQuickGroups(String userId) {
        MutableLiveData<List<Group>> quickGroups = new MutableLiveData<>();
        firestore.collection("groups")
                .whereEqualTo("groupType", Group.GroupType.QUICK)
                .whereArrayContains("members", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        quickGroups.setValue(null);
                        return;
                    }
                    List<Group> groups = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Group group = doc.toObject(Group.class);
                            if (group != null) {
                                group.setGroupId(doc.getId());
                                groups.add(group);
                            }
                        }
                    }
                    quickGroups.setValue(groups);
                });
        return quickGroups;
    }

    public Task<Void> createGroup(Group group) {
        if (group.getGroupType() == Group.GroupType.LIVE) {
            // Generate a unique room code for Live Groups
            String roomCode = generateRoomCode();
            group.setRoomCode(roomCode);
        }

        String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        List<String> members = new ArrayList<>();
        members.add(currentUserId);

        // Set ownerId to the current user's ID
        group.setOwnerId(currentUserId);

        // Use a map to correctly handle merging with existing document
        Map<String, Object> groupMap = group.toMap();
        groupMap.put("members", members);

        // Use add() to create a new document, which will automatically generate a unique ID
        return firestore.collection("groups").add(groupMap)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Get the auto-generated ID and update the document
                    String generatedId = Objects.requireNonNull(task.getResult()).getId();
                    group.setGroupId(generatedId);
                    groupMap.put("groupId", generatedId); // Update the map with the new ID

                    // Use set() with merge option to update the document with the generated ID
                    return firestore.collection("groups").document(generatedId).set(groupMap, SetOptions.merge());
                });
    }

    public Task<Void> joinGroup(String roomCode, String userId) {
        return firestore.collection("groups")
                .whereEqualTo("roomCode", roomCode)
                .whereEqualTo("groupType", Group.GroupType.LIVE)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                        throw new Exception("No group found with the provided room code.");
                    }

                    // Assuming only one group can have the same room code
                    DocumentSnapshot groupDoc = task.getResult().getDocuments().get(0);
                    String groupId = groupDoc.getId();

                    // Add the user to the group's members list
                    return firestore.collection("groups").document(groupId)
                            .update("members", FieldValue.arrayUnion(userId));
                });
    }
    public Task<Void> addMemberToQuickGroup(String groupId, String memberName) {
        return firestore.collection("groups").document(groupId)
                .update("members", FieldValue.arrayUnion(memberName))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Member added to group successfully"));
    }
    public LiveData<Group> getGroupById(String groupId) {
        MutableLiveData<Group> groupLiveData = new MutableLiveData<>();
        firestore.collection("groups").document(groupId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        groupLiveData.setValue(null);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Group group = snapshot.toObject(Group.class);
                        groupLiveData.setValue(group);
                    } else {
                        Log.d(TAG, "Current data: null");
                        groupLiveData.setValue(null);
                    }
                });
        return groupLiveData;
    }

    private String generateRoomCode() {
        // Use a combination of uppercase letters and digits
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random rnd = new Random();

        // Generate a 6-character code
        while (code.length() < 6) {
            int index = (int) (rnd.nextFloat() * chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }
}