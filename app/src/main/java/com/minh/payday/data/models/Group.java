package com.minh.payday.data.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements Serializable {
    private String groupId;
    private String groupName;
    private String iconUrl;
    private String ownerId;
    private List<String> members;
    private String description;
    private boolean isOnline; // Add a field to indicate if the group is online or not
    private boolean isSynced; // Field to indicate if the group is synced with Firestore

    // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    public Group() {
    }

    public Group(String groupId, String groupName, String iconUrl, String ownerId,
                 List<String> members, String description, boolean isOnline, boolean isSynced) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = members;
        this.description = description;
        this.isOnline = isOnline;
        this.isSynced = isSynced;
    }

    // Getters
    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<String> getMembers() {
        return members != null ? members : new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    // isOnline getter
    public boolean isOnline() {
        return isOnline;
    }
    public boolean isSynced() {
        return isSynced;
    }

    // Setters
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // isOnline setter
    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }


    // Optional: Convert to map for Firestore
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("groupName", groupName);
        result.put("iconUrl", iconUrl);
        result.put("ownerId", ownerId);
        result.put("members", getMembers());
        result.put("description", description);
        result.put("isOnline", isOnline);
        result.put("isSynced", isSynced);
        return result;
    }
}