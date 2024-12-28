package com.minh.payday.data.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expense implements Serializable {

    private String expenseId;
    private String groupId;
    private double amount;
    private String description;
    private String payerId;            // userId of who paid
    private List<String> participants; // user IDs who share this expense
    private String category;           // e.g., "Food", "Travel", etc.
    private long timestamp;            // storing as Unix epoch
    private String receiptUrl;         // optional image receipt

    public Expense() {
        // Required empty constructor
    }

    public Expense(String expenseId, String groupId, double amount, String description,
                   String payerId, List<String> participants, String category,
                   long timestamp, String receiptUrl) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.amount = amount;
        this.description = description;
        this.payerId = payerId;
        this.participants = participants;
        this.category = category;
        this.timestamp = timestamp;
        this.receiptUrl = receiptUrl;
    }

    // Getters & Setters
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

    public List<String> getParticipants() {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    // Optional: Convert to map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("expenseId", expenseId);
        map.put("groupId", groupId);
        map.put("amount", amount);
        map.put("description", description);
        map.put("payerId", payerId);
        map.put("participants", participants);
        map.put("category", category);
        map.put("timestamp", timestamp);
        map.put("receiptUrl", receiptUrl);
        return map;
    }
}
