package com.minh.payday.ui.groups.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.minh.payday.ui.groups.LiveGroupsFragment;
import com.minh.payday.ui.groups.QuickGroupsFragment;

public class GroupsPagerAdapter extends FragmentStateAdapter {

    public GroupsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LiveGroupsFragment();
        } else {
            return new QuickGroupsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two tabs: Live Groups and Quick Groups
    }
}