package com.minh.payday.data.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a registered user in the app.
 * Users can optionally have an avatar URL. If null, the UI can display
 * a default avatar based on their initials (first + last name).
 */
public class User implements Serializable {

    private String userId;         // Typically matches Firebase uid
    private String firstName;
    private String lastName;
    private String fullName;       // Convenience field (e.g. "John Doe")
    private String email;
    private String avatarUrl;      // URL to profile picture (null if using default)
    private boolean premiumAccount;
    private String role;           // e.g., "NormalUser" or "SuperUser"

    // Required empty constructor for Firebase
    public User() {
    }

    public User(String userId, String firstName, String lastName, String fullName,
                String email, String avatarUrl, boolean premiumAccount, String role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.premiumAccount = premiumAccount;
        this.role = role;
    }

    // Getters & Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullNameIfPossible();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullNameIfPossible();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isPremiumAccount() {
        return premiumAccount;
    }

    public void setPremiumAccount(boolean premiumAccount) {
        this.premiumAccount = premiumAccount;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Helper to update fullName if firstName/lastName change
    private void updateFullNameIfPossible() {
        if (this.firstName != null && this.lastName != null) {
            this.fullName = this.firstName + " " + this.lastName;
        }
    }

    // Optional: toMap for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("fullName", fullName);
        map.put("email", email);
        map.put("avatarUrl", avatarUrl);
        map.put("premiumAccount", premiumAccount);
        map.put("role", role);
        return map;
    }
}
