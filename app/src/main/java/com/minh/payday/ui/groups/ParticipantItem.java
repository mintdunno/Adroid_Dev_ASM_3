package com.minh.payday.ui.groups;

import com.minh.payday.data.models.User;

public class ParticipantItem {
    private User user;
    private String participantId;
    private String displayName;
    private double amount;
    private boolean isUser;

    public ParticipantItem(User user, double amount) {
        this.user = user;
        this.amount = amount;
        this.isUser = true;
        this.displayName = user.getFirstName();
    }

    public ParticipantItem(String participantId, double amount) {
        this.participantId = participantId;
        this.amount = amount;
        this.isUser = false;
        this.displayName = participantId;
    }

    public ParticipantItem(String participantId, String displayName, double amount) {
        this.participantId = participantId;
        this.displayName = displayName;
        this.amount = amount;
        this.isUser = false; // Assuming non-user participants don't have a User object
    }

    public User getUser() {
        return user;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isUser() {
        return isUser;
    }
}