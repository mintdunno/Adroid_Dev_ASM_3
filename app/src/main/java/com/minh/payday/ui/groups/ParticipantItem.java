package com.minh.payday.ui.groups;

import com.minh.payday.data.models.User;

public class ParticipantItem {
    private User user;
    private String participantId;
    private double amount;
    private boolean isUser;

    public ParticipantItem(User user, double amount) {
        this.user = user;
        this.amount = amount;
        this.isUser = true;
    }

    public ParticipantItem(String participantId, double amount) {
        this.participantId = participantId;
        this.amount = amount;
        this.isUser = false;
    }

    public User getUser() {
        return user;
    }
    public boolean isUser() {
        return isUser;
    }

    public String getParticipantId() {
        return participantId;
    }
    public double getAmount() {
        return amount;
    }
}