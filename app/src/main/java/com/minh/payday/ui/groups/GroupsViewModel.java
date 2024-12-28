package com.minh.payday.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;

import java.util.List;

/**
 * Manages group data: create, fetch, add/remove members, etc.
 */
public class GroupsViewModel extends ViewModel {

    private final GroupRepository groupRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    // For caching the user's groups
    private LiveData<List<Group>> userGroupsLiveData;

    public GroupsViewModel() {
        groupRepository = new GroupRepository();
    }

    // ---------------------------------------------------------
    // Fetch groups for a given user
    // ---------------------------------------------------------
    public LiveData<List<Group>> fetchGroupsForUser(String userId) {
        if (userGroupsLiveData == null) {
            userGroupsLiveData = groupRepository.getGroupsForUser(userId);
        }
        return userGroupsLiveData;
    }

    // ---------------------------------------------------------
    // Create or update a group
    // ---------------------------------------------------------
    public void createOrUpdateGroup(Group group) {
        groupRepository.createOrUpdateGroup(group)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        statusMessage.setValue("Group saved successfully!");
                    } else {
                        statusMessage.setValue("Error saving group: "
                                + task.getException().getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // Add a user to the group
    // ---------------------------------------------------------
    public void addUserToGroup(String groupId, String userId) {
        groupRepository.addUserToGroup(groupId, userId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        statusMessage.setValue("User added to group!");
                    } else {
                        statusMessage.setValue("Error adding user: "
                                + task.getException().getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // Remove a user from the group
    // ---------------------------------------------------------
    public void removeUserFromGroup(String groupId, String userId) {
        groupRepository.removeUserFromGroup(groupId, userId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        statusMessage.setValue("User removed from group!");
                    } else {
                        statusMessage.setValue("Error removing user: "
                                + task.getException().getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // Get single group details
    // ---------------------------------------------------------
    public LiveData<Group> getGroupById(String groupId) {
        return groupRepository.getGroupById(groupId);
    }

    // ---------------------------------------------------------
    // LiveData to observe messages (snackbars, toasts)
    // ---------------------------------------------------------
    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }
}
