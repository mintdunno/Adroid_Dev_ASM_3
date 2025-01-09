package com.minh.payday.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.minh.payday.R;
import com.minh.payday.ui.groups.adapters.GroupsPagerAdapter;

public class GroupsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExtendedFloatingActionButton createGroupFab, joinGroupFab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        createGroupFab = view.findViewById(R.id.createGroupFab);
        joinGroupFab = view.findViewById(R.id.joinGroupFab);

        viewPager.setAdapter(new GroupsPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Live Groups");
                    } else {
                        tab.setText("Quick Groups");
                    }
                }
        ).attach();

        createGroupFab.setOnClickListener(v -> {
            new CreateGroupDialogFragment().show(getChildFragmentManager(), "CreateGroupDialogFragment");
        });

        joinGroupFab.setOnClickListener(v -> {
            new JoinGroupDialogFragment().show(getChildFragmentManager(), "JoinGroupDialogFragment");
        });

        return view;
    }
}