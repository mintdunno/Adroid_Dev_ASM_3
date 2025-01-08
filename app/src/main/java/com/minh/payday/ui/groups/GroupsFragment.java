package com.minh.payday.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.data.models.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    private GroupsViewModel groupsViewModel;
    private FloatingActionButton createGroupFab;
    private Button joinGroupButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appBarLayout;
    private GroupPagerAdapter groupPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        appBarLayout = view.findViewById(R.id.appBarLayout);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        createGroupFab = view.findViewById(R.id.createGroupFab);
        joinGroupButton = view.findViewById(R.id.joinGroupButton);

        // Set up the ViewPager and TabLayout
        setupViewPagerAndTabs();

        // Set up the RecyclerView and observe groups
        observeGroups();

        // Set up click listeners for buttons
        setupClickListeners();

        return view;
    }

    private void setupViewPagerAndTabs() {
        groupPagerAdapter = new GroupPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(groupPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void observeGroups() {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            groupsViewModel.fetchGroupsForUser(currentUserId);

            // Observe changes in the LiveData
            groupsViewModel.getUserGroupsLiveData().observe(getViewLifecycleOwner(), groups -> {
                if (groups != null) {
                    Log.d("GroupsFragment", "Groups received: " + groups.size());
                    for (Group group : groups) {
                        Log.d("GroupsFragment", "Group Name: " + group.getGroupName() +
                                ", isOnline: " + group.isOnline() +
                                ", isSynced: " + group.isSynced() +
                                ", Owner ID: " + group.getOwnerId());
                    }

                    // Update the adapter and notify about the data change
                    groupPagerAdapter.setGroups(groups);
                    groupPagerAdapter.notifyDataSetChanged();
                }
            });

            // Observe the status message LiveData for updates from the ViewModel
            groupsViewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupClickListeners() {
        createGroupFab.setOnClickListener(v -> showCreateGroupDialog());
        joinGroupButton.setOnClickListener(v -> showJoinGroupDialog());
    }

    private void showCreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.create_or_join_group)
                .setItems(new String[]{
                        getString(R.string.create_online_group),
                        getString(R.string.create_offline_group),
                        getString(R.string.join_group)}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startActivity(new Intent(getActivity(), CreateOnlineGroupActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getActivity(), CreateOfflineGroupActivity.class));
                            break;
                        case 2:
                            showJoinGroupDialog();
                            break;
                    }
                });
        builder.create().show();
    }

    private void showJoinGroupDialog() {
        // Implement a dialog to input group code and join
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }
}