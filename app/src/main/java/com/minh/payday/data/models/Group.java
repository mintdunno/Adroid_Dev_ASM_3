package com.minh.payday.data.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Group of users who share expenses.
 * Can have an optional iconUrl for a group icon/avatar.
 */
public class Group implements Serializable {

    private String groupId;
    private String groupName;
    private String iconUrl;        // optional group icon
    private String ownerId;        // userId of the group owner (creator)
    private List<String> members;  // user IDs
    private String description;    // optional group description

    public Group() {
        // Required empty constructor
    }

    public Group(String groupId, String groupName, String iconUrl, String ownerId,
                 List<String> members, String description) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = members;
        this.description = description;
    }

    // Getters & Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getMembers() {
        if (members == null) {
            members = new ArrayList<>();
        }
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Optional: Convert to map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("groupName", groupName);
        map.put("iconUrl", iconUrl);
        map.put("ownerId", ownerId);
        map.put("members", members);
        map.put("description", description);
        return map;
    }
}
