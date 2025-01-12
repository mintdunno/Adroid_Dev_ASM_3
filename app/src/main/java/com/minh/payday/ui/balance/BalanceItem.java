package com.minh.payday.ui.balance;

public class BalanceItem {
    private String participantId; // Could be user ID or guest identifier
    private String displayName;
    private double amount;

    public BalanceItem(String participantId, String displayName, double amount) {
        this.participantId = participantId;
        this.displayName = displayName;
        this.amount = amount;
    }

    // Getters and setters
    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}