package cycloneSounds.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

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
        chatMessageRepository.save(message);
    }

    /**
     * Fetches all messages exchanged between two users, ordered by time.
     * This is called by your ChatMessageController.
     */
    public List<ChatMessage> getChatHistory(String user1, String user2) {


        List<ChatMessage> messagesSent = chatMessageRepository.findBySenderAndReceiver(user1, user2);
        List<ChatMessage> messagesReceived = chatMessageRepository.findBySenderAndReceiver(user2, user1);
        List<ChatMessage> chatHistory = new ArrayList<>(messagesSent);
        chatHistory.addAll(messagesReceived);

        chatHistory.sort(Comparator.comparing(ChatMessage::getTimestamp));

        return chatHistory;
    }
}