package com.minh.payday.ui.expense;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minh.payday.data.models.Expense;
import com.minh.payday.data.repository.ExpenseRepository;

import java.util.List;

/**
 * Manages expense data for a given group: add new expense,
 * list all expenses, track changes in real time, etc.
 */
public class ExpenseViewModel extends ViewModel {

    private final ExpenseRepository expenseRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    // Cache the expenses for a certain group
    private LiveData<List<Expense>> groupExpensesLiveData;

    public ExpenseViewModel() {
        expenseRepository = new ExpenseRepository();
    }

    // ---------------------------------------------------------
    // Load expenses for a given group (real-time if using snapshot)
    // ---------------------------------------------------------
    public LiveData<List<Expense>> loadExpensesForGroup(String groupId) {
        if (groupExpensesLiveData == null) {
            groupExpensesLiveData = expenseRepository.getExpensesByGroup(groupId);
        }
        return groupExpensesLiveData;
    }

    // ---------------------------------------------------------
    // Add or update an expense
    // ---------------------------------------------------------
    public void addOrUpdateExpense(Expense expense) {
        expenseRepository.addOrUpdateExpense(expense)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        statusMessage.setValue("Expense saved successfully!");
                    } else {
                        statusMessage.setValue("Error saving expense: "
                                + task.getException().getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // Expose status messages (for snackbars, toasts)
    // ---------------------------------------------------------
    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }
}
