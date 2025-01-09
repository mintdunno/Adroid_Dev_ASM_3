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
    private List<String> members; // member is user id
    private String description;
    private boolean isTemp; // Field to indicate if it's a "Temp Group" (false = online group, true = temp group)

    // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    public Group() {
    }

    public Group(String groupId, String groupName, String iconUrl, String ownerId,
                 List<String> members, String description, boolean isTemp) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = members;
        this.description = description;
        this.isTemp = isTemp;
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
    public boolean isTemp() {
        return isTemp;
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
    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    // Convert to map for Firestore
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("groupName", groupName);
        result.put("iconUrl", iconUrl);
        result.put("ownerId", ownerId);
        result.put("members", getMembers());
        result.put("description", description);
        result.put("isTemp", isTemp);
        return result;
    }
}