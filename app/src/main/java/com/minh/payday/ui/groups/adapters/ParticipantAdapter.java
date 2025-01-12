package com.minh.payday.ui.groups.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.data.models.User;
import com.minh.payday.ui.groups.ParticipantItem;

import java.util.List;
import java.util.Locale;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private List<ParticipantItem> participantItems;
    private String currentUserId;

    public ParticipantAdapter(List<ParticipantItem> participantItems, String currentUserId) {
        this.participantItems = participantItems;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_item, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        ParticipantItem item = participantItems.get(position);
        User participant = item.getUser();
        double amount = item.getAmount();

        holder.participantNameTextView.setText(participant.getFirstName());
        holder.participantAmountTextView.setText(String.format(Locale.getDefault(), "$%.2f", amount));

        // Check if the current participant is the current user
        if (participant.getUserId().equals(currentUserId)) {
            holder.itemView.setBackgroundResource(R.drawable.rounded_background); // Change background for current user
        } else {
            holder.itemView.setBackgroundResource(R.drawable.rounded_background_light_orange); // Default background
        }
    }

    @Override
    public int getItemCount() {
        return participantItems.size();
    }

    static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        ImageView participantAvatarImageView;
        TextView participantNameTextView, participantAmountTextView;

        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            participantAvatarImageView = itemView.findViewById(R.id.participantAvatarImageView);
            participantNameTextView = itemView.findViewById(R.id.participantNameTextView);
            participantAmountTextView = itemView.findViewById(R.id.participantAmountTextView);
        }
    }
}