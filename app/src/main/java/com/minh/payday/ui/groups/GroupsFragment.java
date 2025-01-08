package com.minh.payday.ui.groups;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.groups.GroupsAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    private GroupsViewModel groupsViewModel;
    private RecyclerView groupsRecyclerView;
    private GroupsAdapter groupsAdapter;
    private FloatingActionButton createGroupFab;
    private Button joinGroupButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);
        createGroupFab = view.findViewById(R.id.createGroupFab);
        joinGroupButton = view.findViewById(R.id.joinGroupButton);

        setupRecyclerView();
        observeGroups();
        setupClickListeners();

        return view;
    }

    private void setupRecyclerView() {
        groupsAdapter = new GroupsAdapter(new ArrayList<>(), group -> {
            // Navigate to GroupDetailsActivity when a group is clicked
            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("groupId", group.getGroupId());
            startActivity(intent);
        });
        groupsRecyclerView.setAdapter(groupsAdapter);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeGroups() {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            groupsViewModel.fetchGroupsForUser(currentUserId);
            groupsViewModel.getUserGroupsLiveData().observe(getViewLifecycleOwner(), groups -> {
                if (groups != null) {
                    groupsAdapter.setGroups(groups);
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
        // Assuming you are using Firebase Authentication
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }
}