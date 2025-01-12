package com.minh.payday.ui.groups.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.ui.groups.AddExpenseActivity;

import java.util.ArrayList;
import java.util.List;

public class MemberSplitAdapter extends RecyclerView.Adapter<MemberSplitAdapter.MemberViewHolder> {

    private List<String> memberIds;
    private List<String> memberNames;
    private List<CheckBox> memberCheckBoxes;
    private List<Double> memberAmounts;
    private List<String> selectedMemberIds;

    public MemberSplitAdapter(List<String> memberIds, List<String> memberNames) {
        this.memberIds = memberIds;
        this.memberNames = memberNames;
        this.memberCheckBoxes = new ArrayList<>();
        this.memberAmounts = new ArrayList<>();
        this.selectedMemberIds = new ArrayList<>();
        for (int i = 0; i < memberNames.size(); i++) {
            memberCheckBoxes.add(null);
            memberAmounts.add(0.0);
            // Initially, all members are selected
            this.selectedMemberIds.add(memberIds.get(i));
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
        holder.memberCheckBox.setChecked(selectedMemberIds.contains(memberIds.get(position)));

        holder.memberCheckBox.setOnCheckedChangeListener(null);
        holder.memberCheckBox.setChecked(selectedMemberIds.contains(memberIds.get(position)));
        holder.memberCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedMemberIds.contains(memberIds.get(position))) {
                    selectedMemberIds.add(memberIds.get(position));
                }
            } else {
                selectedMemberIds.remove(memberIds.get(position));
            }
            // You might want to update the split amounts here
            // Call a method in AddExpenseActivity to recalculate and update amounts
            ((AddExpenseActivity) holder.itemView.getContext()).updateSplitAmounts();
        });

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
        return selectedMemberIds;
    }

    public void selectAllMembers() {
        selectedMemberIds.clear();
        for (String id : memberIds) {
            selectedMemberIds.add(id);
        }
        notifyDataSetChanged();
    }

    public String getMemberName(int position) {
        if (position >= 0 && position < memberNames.size()) {
            return memberNames.get(position);
        }
        return null;
    }

    public String getMemberId(int position) {
        if (position >= 0 && position < memberIds.size()) {
            return memberIds.get(position);
        }
        return null;
    }

    public List<String> getSelectedMemberIds() {
        return selectedMemberIds;
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