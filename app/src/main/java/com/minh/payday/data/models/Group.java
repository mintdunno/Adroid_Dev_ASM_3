package com.minh.payday.data.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements Serializable {
    public enum GroupType {
        LIVE, QUICK
    }

    private String groupId;
    private String groupName;
    private String iconUrl;
    private String ownerId;
    private List<String> members;
    private String description;
    private GroupType groupType;
    private String roomCode; // Only for Live Groups

    // Required empty constructor for Firestore
    public Group() {
    }

    public Group(String groupId, String groupName, String iconUrl, String ownerId,
                 List<String> members, String description, GroupType groupType, String roomCode) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.members = members;
        this.description = description;
        this.groupType = groupType;
        this.roomCode = roomCode;
    }

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

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
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
        map.put("groupType", groupType);
        map.put("roomCode", roomCode);
        return map;
    }
}