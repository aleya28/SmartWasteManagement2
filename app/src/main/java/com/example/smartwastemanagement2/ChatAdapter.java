package com.example.smartwastemanagement2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying chat messages in a RecyclerView
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private final List<ChatMessage> messageList;
    private final Context context;
    
    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }
    
    @Override
    public int getItemViewType(int position) {
        // Return view type based on message type
        return messageList.get(position).getMessageType();
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_bot, parent, false);
            return new BotMessageViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        
        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
            userHolder.messageText.setText(message.getMessage());
            userHolder.timeText.setText(message.getFormattedTime());
        } else if (holder instanceof BotMessageViewHolder) {
            BotMessageViewHolder botHolder = (BotMessageViewHolder) holder;
            botHolder.messageText.setText(message.getMessage());
            botHolder.timeText.setText(message.getFormattedTime());
        }
    }
    
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    
    /**
     * Add a new message to the chat
     */
    public void addMessage(ChatMessage message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }
    
    /**
     * ViewHolder for user messages
     */
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        
        UserMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_user);
            timeText = itemView.findViewById(R.id.text_time_user);
        }
    }
    
    /**
     * ViewHolder for bot messages
     */
    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        
        BotMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_bot);
            timeText = itemView.findViewById(R.id.text_time_bot);
        }
    }
}
