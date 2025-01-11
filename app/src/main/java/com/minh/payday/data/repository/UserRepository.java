package com.minh.payday.data.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.minh.payday.data.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    public UserRepository() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    // ---------------------------------------------------------
    // 1) Register using email/password
    // ---------------------------------------------------------
    public Task<AuthResult> registerUser(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    // ---------------------------------------------------------
    // 2) Login using email/password
    // ---------------------------------------------------------
    public Task<AuthResult> loginUser(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    // ---------------------------------------------------------
    // 3) Create / Update User Document in Firestore
    // ---------------------------------------------------------
    // Use this after registration to set user details (firstName, lastName, avatarUrl, etc.)
    public Task<Void> createOrUpdateUserInFirestore(User user) {
        if (user.getUserId() == null) {
            // If userId is null, we can't proceed. Usually we get userId from auth.getCurrentUser().getUid()
            return Tasks.forException(new Exception("User ID is null."));
        }
        return firestore.collection("users").document(user.getUserId())
                .set(user.toMap());
    }

    // ---------------------------------------------------------
    // 4) Fetch User Document
    // ---------------------------------------------------------
    public LiveData<User> fetchUserById(String userId) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        firestore.collection("users").document(userId)
                .addSnapshotListener((docSnapshot, e) -> {
                    if (e != null) {
                        // Handle error
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (docSnapshot != null && docSnapshot.exists()) {
                        User fetchedUser = docSnapshot.toObject(User.class);
                        userLiveData.setValue(fetchedUser);
                    } else {
                        Log.d(TAG, "User document not found.");
                        userLiveData.setValue(null);
                    }
                });
        return userLiveData;
    }

    // ---------------------------------------------------------
    // 5) Update Avatar URL
    // ---------------------------------------------------------
    // If you store images in Firebase Storage, you'll get a downloadUrl
    // then call this method to set the user's avatarUrl.
    public Task<Void> updateAvatarUrl(String userId, String avatarUrl) {
        return firestore.collection("users").document(userId)
                .update("avatarUrl", avatarUrl);
    }

    // ---------------------------------------------------------
    // 6) Check if user is logged in
    // ---------------------------------------------------------
    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    // ---------------------------------------------------------
    // 7) Get current user ID from Auth
    // ---------------------------------------------------------
    public String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

}
