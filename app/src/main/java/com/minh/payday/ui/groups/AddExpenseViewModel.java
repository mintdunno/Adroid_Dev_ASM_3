package com.minh.payday.ui.groups;

import androidx.lifecycle.ViewModel;

import com.minh.payday.data.models.Expense;
import com.minh.payday.data.repository.ExpenseRepository;

public class AddExpenseViewModel extends ViewModel {

    private ExpenseRepository expenseRepository;

    public AddExpenseViewModel() {
        expenseRepository = new ExpenseRepository();
    }

    public void addExpense(Expense expense) {
        expenseRepository.addOrUpdateExpense(expense);
    }
}