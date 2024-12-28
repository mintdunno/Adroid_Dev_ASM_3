package com.minh.payday.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.minh.payday.data.models.Message;
import com.minh.payday.data.repository.ChatRepository;

import java.util.List;

/**
 * Handles real-time chat via Realtime Database for a specific group.
 */
public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    private LiveData<List<Message>> messagesLiveData;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
    }

    // ---------------------------------------------------------
    // Listen to messages in real-time for a given group
    // ---------------------------------------------------------
    public LiveData<List<Message>> listenToMessages(String groupId) {
        if (messagesLiveData == null) {
            messagesLiveData = chatRepository.listenToMessages(groupId);
        }
        return messagesLiveData;
    }

    // ---------------------------------------------------------
    // Send a new chat message
    // ---------------------------------------------------------
    public void sendMessage(String groupId, Message message) {
        chatRepository.sendMessage(groupId, message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        statusMessage.setValue("Message sent!");
                    } else {
                        statusMessage.setValue("Error sending message: "
                                + (task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error"));
                    }
                });
    }

    // ---------------------------------------------------------
    // Expose status messages
    // ---------------------------------------------------------
    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }
}
