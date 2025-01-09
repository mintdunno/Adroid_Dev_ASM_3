package com.minh.payday.ui.groups;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.data.models.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupPagerAdapter extends FragmentPagerAdapter {
    private List<Group> groups = new ArrayList<>();

    public GroupPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("GroupPagerAdapter", "getItem called for position: " + position);
        List<Group> filteredGroups = new ArrayList<>();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        switch (position) {
            case 0: // All Groups
                Log.d("GroupPagerAdapter", "All Groups tab selected");
                filteredGroups.addAll(groups);
                break;
            case 1: // Online Groups
                Log.d("GroupPagerAdapter", "Online Groups tab selected");
                for (Group group : groups) {
                    if (!group.isTemp() && group.getMembers().contains(currentUserId)) {
                        filteredGroups.add(group);
                    }
                }
                break;
            case 2: // Temp Groups (formerly Offline Groups)
                Log.d("GroupPagerAdapter", "Temp Groups tab selected");
                for (Group group : groups) {
                    if (group.isTemp() && group.getOwnerId() != null && group.getOwnerId().equals(currentUserId)) {
                        filteredGroups.add(group);
                    }
                }
                break;
        }
        Log.d("GroupPagerAdapter", "Filtered groups size: " + filteredGroups.size());
        return GroupListFragment.newInstance(filteredGroups);
    }

    @Override
    public int getCount() {
        // Fixed number of tabs
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Groups";
            case 1:
                return "Online Groups";
            case 2:
                return "Temp Groups"; // Changed from "Offline Groups"
            default:
                return null;
        }
    }
}