package com.minh.payday.ui.groups.adapters;

import android.content.Intent;
import android.util.Log;
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
import com.minh.payday.ui.groups.LiveGroupDetailsActivity;
import com.minh.payday.ui.groups.QuickGroupDetailsActivity;

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

        holder.itemView.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Group currentGroup = groups.get(currentPosition);

                // Determine the type of the group and launch appropriate activity
                if (currentGroup.getGroupType() == Group.GroupType.LIVE) {
                    // Launch LiveGroupDetailsActivity for LIVE groups
                    Intent intent = new Intent(holder.itemView.getContext(), LiveGroupDetailsActivity.class);
                    intent.putExtra(LiveGroupDetailsActivity.EXTRA_GROUP_ID, currentGroup.getGroupId());
                    holder.itemView.getContext().startActivity(intent);
                } else {
                    // Launch QuickGroupDetailsActivity for QUICK groups
                    Intent intent = new Intent(holder.itemView.getContext(), QuickGroupDetailsActivity.class);
                    intent.putExtra(QuickGroupDetailsActivity.EXTRA_GROUP_ID, currentGroup.getGroupId());
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
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