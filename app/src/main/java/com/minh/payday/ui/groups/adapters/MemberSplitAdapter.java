package com.minh.payday.ui.groups.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;

import java.util.ArrayList;
import java.util.List;

public class MemberSplitAdapter extends RecyclerView.Adapter<MemberSplitAdapter.MemberViewHolder> {

    private List<String> memberNames;
    private List<CheckBox> memberCheckBoxes;
    private List<Double> memberAmounts; // List to store amounts for each member

    public MemberSplitAdapter(List<String> memberNames) {
        this.memberNames = memberNames;
        this.memberCheckBoxes = new ArrayList<>();
        this.memberAmounts = new ArrayList<>();
        for (int i = 0; i < memberNames.size(); i++) {
            memberCheckBoxes.add(null); // Initialize checkboxes with nulls
            memberAmounts.add(0.0); // Initialize amounts with 0.0
        }
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_split_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String memberName = memberNames.get(position);
        holder.memberNameTextView.setText(memberName);
        holder.memberCheckBox.setChecked(true); // Set initial state as checked

        // Keep track of checkboxes
        if (memberCheckBoxes.size() > position) {
            memberCheckBoxes.set(position, holder.memberCheckBox);
        }

        // Set the amount for each member
        if (memberAmounts.size() > position) {
            holder.memberAmountTextView.setText(String.format("$%.2f", memberAmounts.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return memberNames.size();
    }

    public void updateMemberAmount(int position, double amount) {
        if (position >= 0 && position < memberAmounts.size()) {
            memberAmounts.set(position, amount);
            notifyItemChanged(position);
        }
    }

    public List<String> getSelectedMembers() {
        List<String> selectedMembers = new ArrayList<>();
        for (int i = 0; i < memberNames.size(); i++) {
            if (memberCheckBoxes.get(i) != null && memberCheckBoxes.get(i).isChecked()) {
                selectedMembers.add(memberNames.get(i));
            }
        }
        return selectedMembers;
    }

    public void clearSelectedMembers() {
        for (CheckBox checkBox : memberCheckBoxes) {
            if (checkBox != null) {
                checkBox.setChecked(false);
            }
        }
    }

    public String getMemberName(int position) {
        if (position >= 0 && position < memberNames.size()) {
            return memberNames.get(position);
        }
        return null;
    }

    // Method to update member names
    public void updateMemberNames(List<String> newMemberNames) {
        this.memberNames.clear();
        this.memberNames.addAll(newMemberNames);
        this.memberCheckBoxes.clear();
        this.memberAmounts.clear();
        for (int i = 0; i < memberNames.size(); i++) {
            memberCheckBoxes.add(null);
            memberAmounts.add(0.0);
        }
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        CheckBox memberCheckBox;
        TextView memberNameTextView;
        TextView memberAmountTextView;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberCheckBox = itemView.findViewById(R.id.memberCheckBox);
            memberNameTextView = itemView.findViewById(R.id.memberNameTextView);
            memberAmountTextView = itemView.findViewById(R.id.memberAmountTextView);
        }
    }
}