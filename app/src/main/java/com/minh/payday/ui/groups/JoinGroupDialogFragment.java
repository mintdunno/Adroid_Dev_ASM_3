package com.minh.payday.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.minh.payday.R;
import com.minh.payday.data.repository.GroupRepository;

public class JoinGroupDialogFragment extends DialogFragment {

    private GroupRepository groupRepository;
    private FirebaseAuth firebaseAuth;

    public JoinGroupDialogFragment() {
        groupRepository = new GroupRepository();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_join_group, null);

        final EditText roomCodeEditText = view.findViewById(R.id.roomCodeEditText);

        builder.setView(view)
                .setPositiveButton("Join", (dialog, id) -> {
                    String roomCode = roomCodeEditText.getText().toString();
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    groupRepository.joinGroup(roomCode, userId);
                })
                .setNegativeButton("Cancel", (dialog, id) ->
                        JoinGroupDialogFragment.this.getDialog().cancel()
                );
        return builder.create();
    }
}