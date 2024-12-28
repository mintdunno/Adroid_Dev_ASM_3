package com.minh.payday.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.minh.payday.data.models.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupRepository {

    private final FirebaseFirestore firestore;

    public GroupRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    // ---------------------------------------------------------
    // 1) Create or Update Group
    // ---------------------------------------------------------
    public Task<Void> createOrUpdateGroup(Group group) {
        if (group.getGroupId() == null) {
            // generate a groupId if not provided
            group.setGroupId(firestore.collection("groups").document().getId());
        }
        DocumentReference docRef = firestore.collection("groups")
                .document(group.getGroupId());
        return docRef.set(group.toMap(), SetOptions.merge());
    }

    // ---------------------------------------------------------
    // 2) Get Groups for a specific user
    // ---------------------------------------------------------
    public LiveData<List<Group>> getGroupsForUser(String userId) {
        MutableLiveData<List<Group>> groupsLiveData = new MutableLiveData<>();
        firestore.collection("groups")
                .whereArrayContains("members", userId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null || querySnapshot == null) {
                        // handle error
                        return;
                    }
                    List<Group> groupList = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Group g = doc.toObject(Group.class);
                        if (g != null) {
                            g.setGroupId(doc.getId()); // ensure groupId is set
                            groupList.add(g);
                        }
                    }
                    groupsLiveData.setValue(groupList);
                });
        return groupsLiveData;
    }

    // ---------------------------------------------------------
    // 3) Add a user to the group (for invites or joining)
    // ---------------------------------------------------------
    public Task<Void> addUserToGroup(String groupId, String userId) {
        return firestore.collection("groups").document(groupId)
                .update("members", com.google.firebase.firestore.FieldValue.arrayUnion(userId));
    }

    // ---------------------------------------------------------
    // 4) Remove a user from the group
    // ---------------------------------------------------------
    public Task<Void> removeUserFromGroup(String groupId, String userId) {
        return firestore.collection("groups").document(groupId)
                .update("members", com.google.firebase.firestore.FieldValue.arrayRemove(userId));
    }

    // ---------------------------------------------------------
    // 5) Fetch single group details
    // ---------------------------------------------------------
    public LiveData<Group> getGroupById(String groupId) {
        MutableLiveData<Group> groupLiveData = new MutableLiveData<>();
        firestore.collection("groups").document(groupId)
                .addSnapshotListener((docSnapshot, e) -> {
                    if (e != null || docSnapshot == null || !docSnapshot.exists()) {
                        // handle error or group not found
                        return;
                    }
                    Group group = docSnapshot.toObject(Group.class);
                    if (group != null) {
                        group.setGroupId(docSnapshot.getId());
                    }
                    groupLiveData.setValue(group);
                });
        return groupLiveData;
    }
}
