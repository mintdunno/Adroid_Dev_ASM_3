package com.minh.payday.ui.groups;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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

        // Filter groups based on the selected tab
        switch (position) {
            case 0: // All Groups
                Log.d("GroupPagerAdapter", "All Groups tab selected");
                filteredGroups = new ArrayList<>(groups); // Create a new list containing all groups
                break;
            case 1: // Online Groups
                Log.d("GroupPagerAdapter", "Online Groups tab selected");
                filteredGroups = new ArrayList<>(groups.stream()
                        .filter(Group::isOnline)
                        .collect(Collectors.toList()));
                break;
            case 2: // Offline Groups
                Log.d("GroupPagerAdapter", "Offline Groups tab selected");
                filteredGroups = new ArrayList<>(groups.stream()
                        .filter(group -> !group.isOnline())
                        .collect(Collectors.toList()));
                break;
            default:
                Log.d("GroupPagerAdapter", "Default case triggered");
                filteredGroups = new ArrayList<>(groups); // Default to all groups
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
        // Set tab titles
        switch (position) {
            case 0:
                return "All Groups";
            case 1:
                return "Online Groups";
            case 2:
                return "Offline Groups";
            default:
                return null;
        }
    }
}