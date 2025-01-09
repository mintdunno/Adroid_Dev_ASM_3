package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.groups.adapters.LiveGroupsAdapter;

import java.util.ArrayList;
import java.util.List;

public class LiveGroupsFragment extends Fragment {

    private RecyclerView liveGroupsRecyclerView;
    private LiveGroupsAdapter adapter;
    private LiveGroupsViewModel viewModel;
    private static final String TAG = "LiveGroupsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LiveGroupsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_groups, container, false);
        liveGroupsRecyclerView = view.findViewById(R.id.liveGroupsRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        liveGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LiveGroupsAdapter(new ArrayList<>());
        liveGroupsRecyclerView.setAdapter(adapter);

        // Observe the LiveData from ViewModel
        viewModel.getLiveGroups().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(List<Group> groups) {
        if (groups == null) {
            Log.e(TAG, "Error fetching live groups or no data available.");
            // Optionally update UI to show an error message or empty state
            return;
        }
        adapter.updateGroups(groups);
    }
}