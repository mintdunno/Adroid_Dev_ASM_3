package com.minh.payday.data.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {

    private String messageId;
    private String senderId;   // userId of who sent the message
    private String content;    // message text (or could be an image URL)
    private long timestamp;

    public Message() {
        // Required empty constructor
    }

    public Message(String messageId, String senderId, String content, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: toMap
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("senderId", senderId);
        map.put("content", content);
        map.put("timestamp", timestamp);
        return map;
    }
}
