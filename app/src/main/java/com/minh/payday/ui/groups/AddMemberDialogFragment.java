package com.minh.payday.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.minh.payday.R;

public class AddMemberDialogFragment extends DialogFragment {

    public interface AddMemberDialogListener {
        void onMemberAdded(String memberName);
    }

    private AddMemberDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_member, null);

        final EditText memberNameEditText = view.findViewById(R.id.memberNameEditText);

        builder.setView(view)
                .setPositiveButton("Add", (dialog, id) -> {
                    String memberName = memberNameEditText.getText().toString().trim();
                    if (!memberName.isEmpty() && listener != null) {
                        listener.onMemberAdded(memberName);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) ->
                        AddMemberDialogFragment.this.getDialog().cancel()
                );
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddMemberDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddMemberDialogListener");
        }
    }
}