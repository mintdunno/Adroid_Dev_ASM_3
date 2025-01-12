package com.minh.payday.data.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expense implements Serializable {
    private String expenseId;
    private String groupId;
    private double amount;
    private String description; // Title of the expense
    private String payerId; // ID of the user who paid
    private String category; // Optional category
    private List<String> participants; // Names of users who are part of the expense
    private long timestamp; // Timestamp of when the expense was added
    private String receiptUrl; // Optional URL to a receipt image
    private Map<String, Double> memberAmounts; // Map of member names to amounts owed
    private String ownerId; // Add ownerId field

    // Required empty constructor for Firestore
    public Expense() {
    }

    // Constructor (you can add more as needed)
    public Expense(String expenseId, String groupId, double amount, String description,
                   String payerId, List<String> participants, String category,
                   long timestamp, String receiptUrl, Map<String, Double> memberAmounts) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.amount = amount;
        this.description = description;
        this.payerId = payerId;
        this.participants = participants;
        this.category = category;
        this.timestamp = timestamp;
        this.receiptUrl = receiptUrl;
        this.memberAmounts = memberAmounts;
    }


    // Add getter for ownerId
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public Map<String, Double> getMemberAmounts() {
        return memberAmounts;
    }

    public void setMemberAmounts(Map<String, Double> memberAmounts) {
        this.memberAmounts = memberAmounts;
    }
}