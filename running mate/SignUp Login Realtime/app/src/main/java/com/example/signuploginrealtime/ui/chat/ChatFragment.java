package com.example.signuploginrealtime.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.signuploginrealtime.R;
import com.example.signuploginrealtime.ui.profile.ProfileFragment;
import com.example.signuploginrealtime.ui.sport.SportFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private DatabaseReference messagesRef;
    private ArrayList<String> messagesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView messageListView;
    private EditText messageEditText;
    private Button sendButton;
    private static String currentReceiverName = "Default";

    public static void setCurrentReceiverName(String receiverName) {
        currentReceiverName = receiverName;
    }

    public static String getCurrentReceiverName() {
        return currentReceiverName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages");

        messageListView = view.findViewById(R.id.messageListView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, messagesList);
        messageListView.setAdapter(adapter);
        String currentReceiver = getCurrentReceiverName();
        messagesRef.orderByChild("receiver").equalTo(currentReceiver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message newMessage = dataSnapshot.getValue(Message.class);
                String displayText = formatMessage(newMessage);
                messagesList.add(displayText);
                adapter.notifyDataSetChanged();
            }

            // ... 其他方法留空

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString();
                String name = "Default";
                name = ProfileFragment.getCurrentProfileName();
                String receiver = "Default";
                //Log.d("DEBUG_TAG", "Profile name: " + name);
                receiver = SportFragment.getOwnerName();
                if (!messageText.isEmpty()) {
                    Message newMessage = new Message(messageText, name,receiver );
                    messagesRef.push().setValue(newMessage);
                    messageEditText.setText("");
                }
            }
        });

        return view;
    }

    private String formatMessage(Message message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = sdf.format(new Date(message.getTimestamp()));
        //String readStatus = message.isRead() ? "已讀" : "未讀";
        return message.getUsername() + ": " + message.getText() + " (" + time +")";
    }
}


