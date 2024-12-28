package com.minh.payday.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minh.payday.data.models.User;
import com.minh.payday.data.repository.UserRepository;

/**
 * Handles all user-related logic: registration, login,
 * profile fetching, avatar updates, etc.
 */
public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    // LiveData representing the currently logged-in user
    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    // For reporting messages or errors back to the UI
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    // ---------------------------------------------------------
    // Register a new user with email/password
    // ---------------------------------------------------------
    public void registerUser(String email, String password,
                             String firstName, String lastName) {

        userRepository.registerUser(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = userRepository.getCurrentUserId();
                        if (userId != null) {
                            // Build user object for Firestore
                            User newUser = new User(
                                    userId,
                                    firstName,
                                    lastName,
                                    firstName + " " + lastName,
                                    email,
                                    null,        // avatarUrl initially null
                                    false,       // premiumAccount
                                    "NormalUser" // role
                            );
                            // Save in Firestore
                            userRepository.createOrUpdateUserInFirestore(newUser)
                                    .addOnCompleteListener(userTask -> {
                                        if (userTask.isSuccessful()) {
                                            currentUserLiveData.setValue(newUser);
                                            statusMessage.setValue("Registration successful!");
                                        } else {
                                            statusMessage.setValue("Failed to save user doc: "
                                                    + userTask.getException().getMessage());
                                        }
                                    });
                        }
                    } else {
                        statusMessage.setValue("Registration error: "
                                + (task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error"));
                    }
                });
    }

    // ---------------------------------------------------------
    // Login existing user with email/password
    // ---------------------------------------------------------
    public void loginUser(String email, String password) {
        userRepository.loginUser(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = userRepository.getCurrentUserId();
                        if (userId != null) {
                            fetchUserById(userId);
                        }
                        statusMessage.setValue("Login successful!");
                    } else {
                        statusMessage.setValue("Login error: "
                                + (task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error"));
                    }
                });
    }

    // ---------------------------------------------------------
    // Fetch user doc from Firestore by userId
    // ---------------------------------------------------------
    public void fetchUserById(String userId) {
        userRepository.fetchUserById(userId).observeForever(fetchedUser -> {
            currentUserLiveData.setValue(fetchedUser);
        });
    }

    // ---------------------------------------------------------
    // Update user’s avatar
    // ---------------------------------------------------------
    public void updateAvatar(String userId, String avatarUrl) {
        userRepository.updateAvatarUrl(userId, avatarUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fetchUserById(userId);
                        statusMessage.setValue("Avatar updated successfully!");
                    } else {
                        statusMessage.setValue("Failed to update avatar: "
                                + task.getException().getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // Check if user is logged in (FirebaseAuth)
    // ---------------------------------------------------------
    public boolean isUserLoggedIn() {
        return userRepository.isUserLoggedIn();
    }

    // ---------------------------------------------------------
    // Get current user ID (if any) from FirebaseAuth
    // ---------------------------------------------------------
    public String getCurrentUserId() {
        return userRepository.getCurrentUserId();
    }

    // ---------------------------------------------------------
    // LiveData getters
    // ---------------------------------------------------------
    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }
}
