package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.ui.groups.adapters.QuickGroupsAdapter;

import java.util.ArrayList;

public class QuickGroupsFragment extends Fragment {

    private RecyclerView quickGroupsRecyclerView;
    private QuickGroupsAdapter adapter;
    private QuickGroupsViewModel viewModel;

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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        quickGroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QuickGroupsAdapter(new ArrayList<>());
        quickGroupsRecyclerView.setAdapter(adapter);

        viewModel.getQuickGroups().observe(getViewLifecycleOwner(), groups -> {
            adapter.updateGroups(groups);
        });
    }
}