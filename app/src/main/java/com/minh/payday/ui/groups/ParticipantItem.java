package com.minh.payday.ui.groups;

import com.minh.payday.data.models.User;

public class ParticipantItem {
    private User user;
    private double amount;

    public ParticipantItem(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }
}