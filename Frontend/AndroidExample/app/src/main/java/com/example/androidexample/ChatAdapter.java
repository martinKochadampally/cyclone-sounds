package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for the chat RecyclerView. This adapter is responsible for displaying
 * chat messages, distinguishing between messages sent by the current user and
 * messages received from others.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List of chat messages to display.
    private final List<ChatMessage> messageList;
    // The username of the current user, used to determine if a message was sent or received.
    private final String currentUsername;

    // View type for messages sent by the current user.
    private static final int VIEW_TYPE_SENT = 1;
    // View type for messages received from another user.
    private static final int VIEW_TYPE_RECEIVED = 2;

    /**
     * Constructs a ChatAdapter.
     *
     * @param messageList The list of ChatMessage objects to be displayed.
     * @param currentUsername The username of the currently logged-in user.
     */
    public ChatAdapter(List<ChatMessage> messageList, String currentUsername) {
        this.messageList = messageList;
        this.currentUsername = currentUsername;
    }

    /**
     * Determines the view type for the item at the given position.
     *
     * @param position The position of the item in the data set.
     * @return VIEW_TYPE_SENT if the message was sent by the current user, VIEW_TYPE_RECEIVED otherwise.
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        if (message.getSender().equals(currentUsername)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            // Inflate the layout for a sent message.
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            // Inflate the layout for a received message.
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        // Bind the message to the appropriate ViewHolder based on the view type.
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /**
     * ViewHolder for displaying sent messages.
     */
    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }

        /**
         * Binds a ChatMessage to the ViewHolder's views.
         *
         * @param message The message to display.
         */
        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
        }
    }

    /**
     * ViewHolder for displaying received messages.
     */
    private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }

        /**
         * Binds a ChatMessage to the ViewHolder's views.
         *
         * @param message The message to display.
         */
        void bind(ChatMessage message) {
            messageText.setText(message.getContent());
        }
    }
}
