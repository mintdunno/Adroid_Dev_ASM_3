package com.minh.payday.ui.groups;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;

public class QuickGroupDetailsViewModel extends ViewModel {

    private GroupRepository groupRepository;
    private MutableLiveData<Group> groupDetails;
    private MutableLiveData<Boolean> groupDeletionStatus;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public QuickGroupDetailsViewModel() {
        groupRepository = new GroupRepository();
    }

    public LiveData<Group> getGroupDetails(String groupId) {
        if (groupDetails == null) {
            groupDetails = new MutableLiveData<>();
            loadGroupDetails(groupId);
        }
        return groupDetails;
    }

    private void loadGroupDetails(String groupId) {
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Group group = documentSnapshot.toObject(Group.class);
                        groupDetails.postValue(group);
                    } else {
                        groupDetails.postValue(null);
                    }
                })
                .addOnFailureListener(e -> groupDetails.postValue(null));
    }

    public LiveData<Boolean> deleteGroup(String groupId) {
        groupDeletionStatus = new MutableLiveData<>();
        db.collection("groups").document(groupId)
                .delete()
                .addOnSuccessListener(aVoid -> groupDeletionStatus.setValue(true))
                .addOnFailureListener(e -> groupDeletionStatus.setValue(false));
        return groupDeletionStatus;
    }
}