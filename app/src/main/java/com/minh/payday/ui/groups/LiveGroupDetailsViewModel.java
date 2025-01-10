package com.minh.payday.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;

public class LiveGroupDetailsViewModel extends ViewModel {

    private GroupRepository groupRepository;
    private MutableLiveData<Group> groupDetails;

    public LiveGroupDetailsViewModel() {
        groupRepository = new GroupRepository();
    }

    public LiveData<Group> getGroupDetails(String groupId) {
        if (groupDetails == null) {
            groupDetails = new MutableLiveData<>();
            loadGroupDetails(groupId);
        }
        return groupDetails;
    }

    private void loadGroupDetails(String groupId) {
        // Fetch group details from repository and post value to groupDetails
    }
}