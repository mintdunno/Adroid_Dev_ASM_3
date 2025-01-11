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

    public MemberSplitAdapter(List<String> memberNames) {
        this.memberNames = memberNames;
        this.memberCheckBoxes = new ArrayList<>();
        for (int i = 0; i < memberNames.size(); i++) {
            memberCheckBoxes.add(null); // Initialize with nulls
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
        holder.memberAmountTextView.setText("$50.00"); // Set initial amount, later calculate based on split

        // Keep track of checkboxes
        if (memberCheckBoxes.size() > position && memberCheckBoxes.get(position) == null) {
            memberCheckBoxes.set(position, holder.memberCheckBox);
        }
    }

    @Override
    public int getItemCount() {
        return memberNames.size();
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

    public String getMemberName(int position) {
        if (position >= 0 && position < memberNames.size()) {
            return memberNames.get(position);
        }
        return null;
    }
}