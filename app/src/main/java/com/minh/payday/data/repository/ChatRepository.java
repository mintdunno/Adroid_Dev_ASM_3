package com.minh.payday.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.*;
import com.minh.payday.data.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private final DatabaseReference chatsRef;

    public ChatRepository() {
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
    }

    // ---------------------------------------------------------
    // 1) Send a message
    // ---------------------------------------------------------
    public Task<Void> sendMessage(String groupId, Message message) {
        String messageId = chatsRef.child(groupId).push().getKey();
        if (messageId == null) {
            return Tasks.forException(new Exception("Failed to generate messageId"));
        }
        message.setMessageId(messageId);
        return chatsRef.child(groupId).child(messageId).setValue(message);
    }

    // ---------------------------------------------------------
    // 2) Listen for messages in real-time
    // ---------------------------------------------------------
    public LiveData<List<Message>> listenToMessages(String groupId) {
        MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

        chatsRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Message> msgList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Message m = child.getValue(Message.class);
                    if (m != null) {
                        msgList.add(m);
                    }
                }
                messagesLiveData.setValue(msgList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // handle error
            }
        });

        return messagesLiveData;
    }
}
