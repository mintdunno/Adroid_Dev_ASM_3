package com.minh.payday.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;
import com.minh.payday.data.repository.UserRepository;

public class CreateGroupDialogFragment extends DialogFragment {
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public CreateGroupDialogFragment() {
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_group, null);
        final EditText groupNameEditText = view.findViewById(R.id.groupNameEditText);
        final RadioGroup groupTypeRadioGroup = view.findViewById(R.id.groupTypeRadioGroup);

        builder.setView(view)
                .setPositiveButton("Create", (dialog, id) -> {
                    String groupName = groupNameEditText.getText().toString();
                    int selectedTypeId = groupTypeRadioGroup.getCheckedRadioButtonId();

                    Group.GroupType groupType = selectedTypeId == R.id.liveGroupRadioButton ?
                            Group.GroupType.LIVE :
                            Group.GroupType.QUICK;

                    Group newGroup = new Group(null, groupName, null, null, null, null, groupType, null);

                    groupRepository.createGroup(newGroup)
                            .addOnSuccessListener(aVoid -> {
                                if (groupType == Group.GroupType.LIVE) {
                                    fetchAndUpdateGroup(newGroup);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CreateGroupDialogFragment", "Error creating group", e);
                            });
                })
                .setNegativeButton("Cancel", (dialog, id) ->
                        CreateGroupDialogFragment.this.getDialog().cancel());

        return builder.create();
    }

    private void fetchAndUpdateGroup(Group group) {
        String currentUserId = userRepository.getCurrentUserId();

        groupRepository.getLiveGroups(currentUserId)
                .observe(this, groups -> {
                    for (Group updatedGroup : groups) {
                        if (updatedGroup.getGroupId() != null && updatedGroup.getGroupId().equals(group.getGroupId())) {
                            showRoomCodeDialog(updatedGroup.getRoomCode());
                            break;
                        }
                    }
                });
    }

    private void showRoomCodeDialog(String roomCode) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Group Created")
                    .setMessage("Your group has been created with the following room code: " + roomCode + "\n" + "Share this code with your friends to let them join the group")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}