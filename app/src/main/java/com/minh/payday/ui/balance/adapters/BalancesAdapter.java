package com.minh.payday.ui.balance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;
import com.minh.payday.ui.balance.BalanceItem;

import java.util.List;
import java.util.Locale;

public class BalancesAdapter extends RecyclerView.Adapter<BalancesAdapter.BalanceViewHolder> {

    private List<BalanceItem> balanceItems;
    private String currentUserId;

    public BalancesAdapter(List<BalanceItem> balanceItems, String currentUserId) {
        this.balanceItems = balanceItems;
        this.currentUserId = currentUserId;
    }

    public void updateBalances(List<BalanceItem> newBalanceItems) {
        this.balanceItems.clear();
        this.balanceItems.addAll(newBalanceItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_list_item, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        BalanceItem item = balanceItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return balanceItems.size();
    }

    class BalanceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivParticipantAvatar;
        TextView tvParticipantName;
        TextView tvParticipantSubtitle;
        TextView tvParticipantBalance;

        public BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivParticipantAvatar = itemView.findViewById(R.id.ivParticipantAvatar);
            tvParticipantName = itemView.findViewById(R.id.tvParticipantName);
            tvParticipantSubtitle = itemView.findViewById(R.id.tvParticipantSubtitle);
            tvParticipantBalance = itemView.findViewById(R.id.tvParticipantBalance);
        }

        public void bind(BalanceItem item) {
            String displayName = item.getDisplayName();
            if (item.getParticipantId().equals(currentUserId)) {
                displayName += " (Me)";
            }
            tvParticipantName.setText(displayName);

            double balance = item.getAmount();
            tvParticipantBalance.setText(String.format(Locale.getDefault(), "$%.2f", balance));
            if (balance > 0) {
                tvParticipantBalance.setTextColor(itemView.getContext().getResources().getColor(R.color.green_primary)); // Or your color for positive balance
            } else if (balance < 0) {
                tvParticipantBalance.setTextColor(itemView.getContext().getResources().getColor(R.color.black)); // Or your color for negative balance
            } else {
                tvParticipantBalance.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray)); // Or your color for zero balance
            }
        }
    }
}