package com.minh.payday.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.minh.payday.data.models.Expense;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {
    private final FirebaseFirestore firestore;

    public ExpenseRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Method to add or update an expense
    public Task<Void> addOrUpdateExpense(Expense expense) {
        if (expense.getExpenseId() == null) {
            // If the expense does not have an ID, create a new one
            expense.setExpenseId(firestore.collection("expenses").document().getId());
        }
        return firestore.collection("expenses")
                .document(expense.getExpenseId())
                .set(expense);
    }

    // Method to get expenses by group
    public LiveData<List<Expense>> getExpensesByGroup(String groupId) {
        MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
        firestore.collection("expenses")
                .whereEqualTo("groupId", groupId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        // Handle error
                        expensesLiveData.setValue(null);
                        return;
                    }
                    List<Expense> expenseList = new ArrayList<>();
                    if (querySnapshot != null) {
                        for (var doc : querySnapshot.getDocuments()) {
                            Expense exp = doc.toObject(Expense.class);
                            if (exp != null) {
                                exp.setExpenseId(doc.getId());
                                expenseList.add(exp);
                            }
                        }
                    }
                    expensesLiveData.setValue(expenseList);
                });
        return expensesLiveData;
    }

}