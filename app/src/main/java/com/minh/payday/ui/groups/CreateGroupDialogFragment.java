package com.minh.payday.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.minh.payday.R;
import com.minh.payday.data.models.Group;
import com.minh.payday.data.repository.GroupRepository;

public class CreateGroupDialogFragment extends DialogFragment {

    private GroupRepository groupRepository;

    public CreateGroupDialogFragment() {
        groupRepository = new GroupRepository();
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
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String groupName = groupNameEditText.getText().toString();
                        int selectedTypeId = groupTypeRadioGroup.getCheckedRadioButtonId();
                        Group.GroupType groupType = selectedTypeId == R.id.liveGroupRadioButton
                                ? Group.GroupType.LIVE
                                : Group.GroupType.QUICK;

                        Group newGroup = new Group(null, groupName, null, null, null, null, groupType, null);
                        groupRepository.createGroup(newGroup);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateGroupDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}