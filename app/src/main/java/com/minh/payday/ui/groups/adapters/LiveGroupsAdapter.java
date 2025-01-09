package com.minh.payday.ui.groups.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;

import java.util.List;

public class LiveGroupsAdapter extends RecyclerView.Adapter<LiveGroupsAdapter.LiveGroupViewHolder> {

    private List<Group> groups;

    public LiveGroupsAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public LiveGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false); // Use your group item layout
        return new LiveGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveGroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupNameTextView.setText(group.getGroupName());
        // Set other data to views
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(List<Group> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    static class LiveGroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView;
        // Other views

        public LiveGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            // Initialize other views
        }
    }
}