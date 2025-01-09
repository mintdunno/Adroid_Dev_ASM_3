package com.minh.payday.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;
import com.minh.payday.data.repository.UserRepository;

import java.util.List;

public class QuickGroupsViewModel extends ViewModel {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public QuickGroupsViewModel() {
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
    }

    public LiveData<List<Group>> getQuickGroups() {
        String currentUserId = userRepository.getCurrentUserId();
        return groupRepository.getQuickGroups(currentUserId);
    }
}