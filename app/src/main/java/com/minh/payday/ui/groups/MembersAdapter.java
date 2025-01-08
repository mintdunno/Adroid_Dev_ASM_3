package com.minh.payday.ui.groups;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minh.payday.R;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private List<String> members;

    public MembersAdapter(List<String> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false); // You need to create item_member.xml
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String member = members.get(position);
        holder.memberNameTextView.setText(member);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setMembers(List<String> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberNameTextView;

        MemberViewHolder(View itemView) {
            super(itemView);
            memberNameTextView = itemView.findViewById(R.id.memberNameTextView); // Make sure you have this ID in your item_member.xml
        }
    }
}