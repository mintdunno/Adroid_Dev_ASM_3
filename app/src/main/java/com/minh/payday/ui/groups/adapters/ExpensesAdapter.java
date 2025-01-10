package com.minh.payday.ui.groups.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.Expense;

import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;

    public ExpensesAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.expenseNameTextView.setText(expense.getDescription());
        holder.expenseAmountTextView.setText(String.format("$%.2f", expense.getAmount()));
        holder.paidByTextView.setText("Paid by " + expense.getPayerId());
        // Set other fields as necessary
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void updateExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView expenseNameTextView;
        TextView expenseAmountTextView;
        TextView paidByTextView;
        // Add other views as needed

        ExpenseViewHolder(View itemView) {
            super(itemView);
            expenseNameTextView = itemView.findViewById(R.id.expenseNameTextView);
            expenseAmountTextView = itemView.findViewById(R.id.expenseAmountTextView);
            paidByTextView = itemView.findViewById(R.id.paidByTextView);
            // Initialize other views
        }
    }
}