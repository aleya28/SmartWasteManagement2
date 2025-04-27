package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    LinearLayout chatContainer;
    EditText inputMessage;
    ImageButton sendBtn;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Init views
        chatContainer = findViewById(R.id.chatContainer);
        inputMessage = findViewById(R.id.editTextMessage);
        sendBtn = findViewById(R.id.buttonSend);
        backBtn = findViewById(R.id.btnBack);

        // Send Button
        sendBtn.setOnClickListener(v -> {
            String userMsg = inputMessage.getText().toString().trim();
            if (!userMsg.isEmpty()) {
                addMessage("You", userMsg);
                inputMessage.setText("");

                String reply = getAutoReply(userMsg);
                addMessage("Bot", reply);
            }
        });

        // Back Button to Homepage
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, HomepageActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addMessage(String sender, String message) {
        TextView textView = new TextView(this);
        textView.setText(sender + ": " + message);
        textView.setPadding(20, 12, 20, 12);
        chatContainer.addView(textView);
    }

    private String getAutoReply(String msg) {
        msg = msg.toLowerCase();
        if (msg.contains("hello") || msg.contains("hi")) {
            return "Hello! How can I assist you today?";
        } else if (msg.contains("booking")) {
            return "You can make a booking via the Booking page.";
        } else if (msg.contains("location")) {
            return "Please go to 'Find Us' to view nearby centers.";
        } else if (msg.contains("payment")) {
            return "You can check payment status in My Submission.";
        } else {
            return "Sorry, I didn't understand. Try: booking, payment, or location.";
        }
    }
}
