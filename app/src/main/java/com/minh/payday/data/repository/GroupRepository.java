package com.minh.payday.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.minh.payday.data.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupRepository {
    private final FirebaseFirestore firestore;

    public GroupRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Group> getGroupById(String groupId) {
        MutableLiveData<Group> groupLiveData = new MutableLiveData<>();
        firestore.collection("groups").document(groupId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.w("GroupRepository", "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Group group = documentSnapshot.toObject(Group.class);
                        if (group != null) {
                            group.setGroupId(documentSnapshot.getId());
                            groupLiveData.setValue(group);
                        }
                    } else {
                        Log.d("GroupRepository", "Current data: null");
                    }
                });
        return groupLiveData;
    }

    public Task<Void> createOrUpdateGroup(Group group) {
        if (group.getGroupId() == null) {
            // Create new group
            return firestore.collection("groups").add(group.toMap())
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Set the generated ID to the group
                        String generatedId = task.getResult().getId();
                        group.setGroupId(generatedId);
                        // Update the document with the ID
                        return firestore.collection("groups").document(generatedId).set(group.toMap(), SetOptions.merge());
                    });
        } else {
            // Update existing group
            return firestore.collection("groups").document(group.getGroupId()).set(group.toMap(), SetOptions.merge());
        }
    }

}