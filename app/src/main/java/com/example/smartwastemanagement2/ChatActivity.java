package com.example.smartwastemanagement2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatActivity handles the user interaction with the Smart Waste Management chatbot.
 * 
 * Features:
 * - Real-time chat with smart bot responses
 * - Chat history persistence between sessions
 * - Option to clear chat history
 */
public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageButton sendBtn;
    private ImageButton backBtn;
    private ChatAdapter chatAdapter;
    private ChatBotHelper chatBotHelper;
    private List<ChatMessage> messageList;
    private DBHelper dbHelper;
    private String currentUserEmail;
    private static final String TAG = "ChatActivity";
    
    // Handler for showing the typing indicator
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize the ChatBotHelper and DBHelper
        chatBotHelper = new ChatBotHelper();
        dbHelper = new DBHelper(this);
        
        // Get current user email
        SharedPreferences prefs = getSharedPreferences("SmartWastePrefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("userEmail", "");
        
        if (currentUserEmail.isEmpty()) {
            Log.e(TAG, "No user email found in shared preferences");
            Toast.makeText(this, "Error loading user information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize message list
        messageList = new ArrayList<>();
        
        // Init views
        recyclerView = findViewById(R.id.recyclerView);
        inputMessage = findViewById(R.id.editTextMessage);
        sendBtn = findViewById(R.id.buttonSend);
        backBtn = findViewById(R.id.btnBack);
        ImageButton menuBtn = findViewById(R.id.btnMenu);
        
        // Setup menu button
        menuBtn.setOnClickListener(v -> showPopupMenu(v));
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Load chat history from database
        loadChatHistory();
        
        // Only show welcome message if this is a new conversation
        if (messageList.isEmpty()) {
            addBotMessage("Hello! I'm your Smart Waste Management assistant. How can I help you today?");
        }

        // Send Button
        sendBtn.setOnClickListener(v -> sendMessage());
        
        // Set up EditText to send on "Done" key
        inputMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Back Button to Homepage
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, HomepageActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    /**
     * Set up the RecyclerView with layout manager and adapter
     */
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        chatAdapter = new ChatAdapter(this, messageList);
        recyclerView.setAdapter(chatAdapter);
    }
    
    /**
     * Send a message from the input field
     */
    private void sendMessage() {
        String userMsg = inputMessage.getText().toString().trim();
        if (!userMsg.isEmpty()) {
            // Add user message to the chat
            addUserMessage(userMsg);
            
            // Clear input field
            inputMessage.setText("");
            
            // Simulate bot "typing" for a more realistic feel
            simulateBotTyping(userMsg);
        }
    }
    
    /**
     * Add a user message to the chat and save to database
     */
    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, ChatMessage.TYPE_USER);
        chatAdapter.addMessage(chatMessage);
        scrollToBottom();
        
        // Save to database
        if (!currentUserEmail.isEmpty()) {
            dbHelper.saveChatMessage(chatMessage, currentUserEmail);
        }
    }
    
    /**
     * Add a bot message to the chat and save to database
     */
    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, ChatMessage.TYPE_BOT);
        chatAdapter.addMessage(chatMessage);
        scrollToBottom();
        
        // Save to database
        if (!currentUserEmail.isEmpty()) {
            dbHelper.saveChatMessage(chatMessage, currentUserEmail);
        }
    }
    
    /**
     * Load chat history from database
     */
    private void loadChatHistory() {
        if (currentUserEmail.isEmpty()) {
            return;
        }
        
        List<ChatMessage> savedMessages = dbHelper.getChatHistory(currentUserEmail);
        
        if (!savedMessages.isEmpty()) {
            messageList.clear();
            messageList.addAll(savedMessages);
            chatAdapter.notifyDataSetChanged();
            scrollToBottom();
            
            Log.d(TAG, "Loaded " + savedMessages.size() + " messages from history");
        } else {
            Log.d(TAG, "No chat history found");
        }
    }
    
    /**
     * Simulate bot typing with a delay for more realistic interaction
     */
    private void simulateBotTyping(String userMessage) {
        // Delay response based on message length (longer message, longer "thinking" time)
        int typingDelay = Math.min(1000, 500 + userMessage.length() * 20);
        
        handler.postDelayed(() -> {
            // Get the bot's response
            String botResponse = chatBotHelper.getResponse(userMessage);
            
            // Add bot's response to the chat
            addBotMessage(botResponse);
        }, typingDelay);
    }
    
    /**
     * Scroll the RecyclerView to show the most recent message
     */
    private void scrollToBottom() {
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }
    
    /**
     * Show the popup menu for chat options
     */
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.chat_menu, popup.getMenu());
        
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear_history) {
                confirmClearHistory();
                return true;
            }
            return false;
        });
        
        popup.show();
    }
    
    /**
     * Show confirmation dialog before clearing chat history
     */
    private void confirmClearHistory() {
        new AlertDialog.Builder(this)
            .setTitle("Clear Chat History")
            .setMessage("Are you sure you want to clear all chat history? This cannot be undone.")
            .setPositiveButton("Clear", (dialog, which) -> {
                clearChatHistory();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Clear chat history from database and UI
     */
    private void clearChatHistory() {
        if (!currentUserEmail.isEmpty()) {
            dbHelper.clearChatHistory(currentUserEmail);
            
            // Clear UI
            messageList.clear();
            chatAdapter.notifyDataSetChanged();
            
            // Add welcome message back
            addBotMessage("Hello! I'm your Smart Waste Management assistant. How can I help you today?");
            
            Toast.makeText(this, "Chat history cleared", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Remove any pending callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}
