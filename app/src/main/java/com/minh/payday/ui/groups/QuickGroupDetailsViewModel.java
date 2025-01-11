package com.minh.payday.ui.groups;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.minh.payday.data.models.Expense;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.ExpenseRepository;
import com.minh.payday.data.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;

public class QuickGroupDetailsViewModel extends ViewModel {

    private GroupRepository groupRepository;
    private ExpenseRepository expenseRepository;
    private MutableLiveData<Group> groupDetails;
    private MutableLiveData<List<Expense>> groupExpenses;
    private MutableLiveData<Boolean> groupDeletionStatus;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public QuickGroupDetailsViewModel() {
        groupRepository = new GroupRepository();
        expenseRepository = new ExpenseRepository();
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

    public LiveData<List<Expense>> getExpenses(String groupId) {
        if (groupExpenses == null) {
            groupExpenses = new MutableLiveData<>();
            loadExpenses(groupId);
        }
        return groupExpenses;
    }

    private void loadExpenses(String groupId) {
        expenseRepository.getExpensesByGroup(groupId).observeForever(expenses -> {
            groupExpenses.setValue(expenses);
        });
    }

    public void addMemberToGroup(String groupId, String memberName) {
        groupRepository.addMemberToQuickGroup(groupId, memberName)
                .addOnSuccessListener(aVoid -> {
                    // Instead of posting a success message, trigger a refresh of group
                    loadGroupDetails(groupId);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("QuickGroupDetailsVM", "Error adding member", e);
                });
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