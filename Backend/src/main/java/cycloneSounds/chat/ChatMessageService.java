package cycloneSounds.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList; // Add this import
import java.util.Comparator; // Add this import

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * This is the method your ChatSocket calls.
     * It creates a new ChatMessage entity and saves it to the database.
     */
    public void saveMessage(String sender, String receiver, String content) {
        ChatMessage message = new ChatMessage(sender, receiver, content);
        chatMessageRepository.save(message); // This will now work
    }

    /**
     * Fetches all messages exchanged between two users, ordered by time.
     * This is called by your ChatMessageController.
     */
    public List<ChatMessage> getChatHistory(String user1, String user2) {


        List<ChatMessage> messagesSent = chatMessageRepository.findBySenderUsernameAndReceiverUsername(user1, user2);
        List<ChatMessage> messagesReceived = chatMessageRepository.findBySenderUsernameAndReceiverUsername(user2, user1);
        List<ChatMessage> chatHistory = new ArrayList<>(messagesSent);
        chatHistory.addAll(messagesReceived);

        chatHistory.sort(Comparator.comparing(ChatMessage::getTimestamp));

        return chatHistory;
    }
}