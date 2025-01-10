package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.ui.groups.adapters.LiveGroupsAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuickGroupsFragment extends Fragment {

    private RecyclerView quickGroupsRecyclerView;
    private LiveGroupsAdapter adapter;
    private QuickGroupsViewModel viewModel;
    private ProgressBar loadingIndicator;
    private TextView errorMessageTextView;
    private static final String TAG = "QuickGroupsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(QuickGroupsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_groups, container, false);
        quickGroupsRecyclerView = view.findViewById(R.id.quickGroupsRecyclerView);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        errorMessageTextView = view.findViewById(R.id.errorMessageTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        quickGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LiveGroupsAdapter(new ArrayList<>());
        quickGroupsRecyclerView.setAdapter(adapter);

        // Observe the LiveData from ViewModel
        viewModel.getQuickGroups().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(List<Group> groups) {
        // First, hide both the RecyclerView and the error message
        quickGroupsRecyclerView.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);

        if (groups == null) {
            // If the groups list is null, show the error message
            Log.e(TAG, "Error fetching quick groups.");
            errorMessageTextView.setVisibility(View.VISIBLE);
            errorMessageTextView.setText("Failed to load groups.");
        } else if (groups.isEmpty()) {
            // If the groups list is empty, show a message indicating no groups
            errorMessageTextView.setVisibility(View.VISIBLE);
            errorMessageTextView.setText("No quick groups available.");
        } else {
            // If the groups list is not empty, show the RecyclerView and update the adapter
            quickGroupsRecyclerView.setVisibility(View.VISIBLE);
            adapter.updateGroups(groups);
        }
    }
}