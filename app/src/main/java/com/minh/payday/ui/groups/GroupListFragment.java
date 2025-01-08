package com.minh.payday.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.groups.GroupsAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment {
    private GroupsAdapter groupsAdapter;
    private RecyclerView groupsRecyclerView;

    public static GroupListFragment newInstance(List<Group> groups) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putSerializable("groups", new ArrayList<>(groups));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Group> groups = (ArrayList<Group>) getArguments().getSerializable("groups");
            Log.d("GroupListFragment", "Received groups size: " + groups.size());
            if (groupsAdapter != null) {
                groupsAdapter.setGroups(groups);
                getActivity().runOnUiThread(() -> groupsAdapter.notifyDataSetChanged());
            }
        }
    }

    private void setupRecyclerView() {
        groupsAdapter = new GroupsAdapter(new ArrayList<>(), group -> {
            Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
            intent.putExtra("groupId", group.getGroupId());
            startActivity(intent);
        });
        groupsRecyclerView.setAdapter(groupsAdapter);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}