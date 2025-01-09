package com.minh.payday.ui.groups.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.minh.payday.R;
import com.minh.payday.data.models.Group;

import java.util.List;

public class LiveGroupsAdapter extends RecyclerView.Adapter<LiveGroupsAdapter.GroupViewHolder> {

    private List<Group> groups;

    public LiveGroupsAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupNameTextView.setText(group.getGroupName());

        // Set group type text
        String groupTypeText = group.getGroupType() == Group.GroupType.LIVE
                ? "Live Group"
                : "Quick Group";
        holder.groupTypeTextView.setText(groupTypeText);

        // Differentiate the appearance based on group type
        if (group.getGroupType() == Group.GroupType.LIVE) {
            // Style for Live Groups
            holder.groupNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.live_group_text_color));
            holder.groupTypeTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.live_group_text_color));
            holder.groupIconImageView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.live_group_icon_color));
            // Set other Live Group specific styles here
        } else {
            // Style for Quick Groups
            holder.groupNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.quick_group_text_color));
            holder.groupTypeTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.quick_group_text_color));
            holder.groupIconImageView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.quick_group_icon_color));
            // Set other Quick Group specific styles here
        }

        // Set group icon
        if (group.getIconUrl() != null && !group.getIconUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(group.getIconUrl())
                    .placeholder(R.drawable.ic_group_placeholder)
                    .into(holder.groupIconImageView);
        } else {
            holder.groupIconImageView.setImageResource(R.drawable.ic_group_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(List<Group> newGroups) {
        this.groups = newGroups;
        notifyDataSetChanged();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView;
        TextView groupTypeTextView;
        ImageView groupIconImageView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            groupTypeTextView = itemView.findViewById(R.id.groupTypeTextView);
            groupIconImageView = itemView.findViewById(R.id.groupIconImageView);
        }
    }
}