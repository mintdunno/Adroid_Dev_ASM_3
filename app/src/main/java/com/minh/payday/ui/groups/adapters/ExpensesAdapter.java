package com.minh.payday.ui.groups.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.Expense;
import com.minh.payday.ui.groups.ExpenseDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

    public ExpensesAdapter(List<Expense> expenses, OnItemClickListener listener) {
        this.expenses = expenses;
        this.listener = listener;
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
        holder.bind(expense, listener);
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
        TextView dateTextView;

        ExpenseViewHolder(View itemView) {
            super(itemView);
            expenseNameTextView = itemView.findViewById(R.id.expenseNameTextView);
            expenseAmountTextView = itemView.findViewById(R.id.expenseAmountTextView);
            paidByTextView = itemView.findViewById(R.id.paidByTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }

        void bind(final Expense expense, final OnItemClickListener listener) {
            expenseNameTextView.setText(expense.getDescription());
            expenseAmountTextView.setText(String.format("$%.2f", expense.getAmount()));
            paidByTextView.setText("Paid by " + expense.getPayerId());
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            String formattedDate = sdf.format(new Date(expense.getTimestamp()));
            dateTextView.setText(formattedDate);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(expense);
                }
            });
        }
    }
}