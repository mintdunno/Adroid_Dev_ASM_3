package com.minh.payday.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minh.payday.R;
import com.sendbird.uikit.fragments.ChannelListFragment;

public class ChatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Load the ChannelListFragment into the container
        loadChannelListFragment();

        return view;
    }

    private void loadChannelListFragment() {
        ChannelListFragment channelListFragment = new ChannelListFragment.Builder()
                .build();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, channelListFragment);
        transaction.commit();
    }
}
