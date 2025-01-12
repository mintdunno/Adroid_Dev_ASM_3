package com.minh.payday.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        groupRepository.joinGroup(roomCode, userId)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Joined group successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("JoinGroupDialogFragment", "Error joining group", e);
                                    Toast.makeText(getContext(), "Failed to join group: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) ->
                        JoinGroupDialogFragment.this.getDialog().cancel()
                );

        return builder.create();
    }
}