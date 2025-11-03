package cycloneSounds.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import cycloneSounds.websocketChats.CustomSpringConfigurator;
import cycloneSounds.websocketChats.MessageSocket;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/chat/{username}", configurator = CustomSpringConfigurator.class)
public class ChatSocket {

    private static final Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private static final Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    private ChatMessageService chatMessageService;

    public void setChatMessageService(ChatMessageService service) {
        this.chatMessageService = service;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        if (username == null || username.isEmpty()) {
            try {
                session.close(new jakarta.websocket.CloseReason(
                        jakarta.websocket.CloseReason.CloseCodes.VIOLATED_POLICY, "Username is required"));
            } catch (IOException e) {
                logger.error("Failed to close session on open", e);
            }
            return;
        }
        logger.info("Entered into Open for user: " + username);
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        logger.info("Got Message: " + message);
        String senderUsername = sessionUsernameMap.get(session);

        if (senderUsername == null) {
            logger.warn("Received message from unknown session");
            return;
        }

        try {
            MessageSocket payload = objectMapper.readValue(message, MessageSocket.class);
            String receiverUsername = payload.getReceiver();
            String content = payload.getContent();

            if (receiverUsername == null || receiverUsername.isEmpty() || content == null || content.isEmpty()) {
                logger.warn("Invalid message payload: " + message);
                return;
            }

            // Check for null service, just in case
            if (chatMessageService == null) {
                logger.error("ChatMessageService is STILL NULL. The Configurator failed.");
                throw new IllegalStateException("Chat service not available.");
            }

            // This should now work
            chatMessageService.saveMessage(senderUsername, receiverUsername, content);

            Session receiverSession = usernameSessionMap.get(receiverUsername);
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.getBasicRemote().sendText(objectMapper.writeValueAsString(payload));
            }
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(payload));

        } catch (Exception e) {
            logger.error("Failed to process message or save to DB", e);
            session.getBasicRemote().sendText("{\"error\":\"Failed to send message: " + e.getMessage() + "\"}");
        }
    }

    @OnClose
    public void onClose(Session session) {
        String username = sessionUsernameMap.get(session);
        if (username != null) {
            logger.info("Entered into Close for user: " + username);
            sessionUsernameMap.remove(session);
            usernameSessionMap.remove(username);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket Error for session " + session.getId(), throwable);
        try {
            session.close(new jakarta.websocket.CloseReason(
                    jakarta.websocket.CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
        } catch (IOException e) {
            logger.error("Failed to close session on error", e);
        }
    }
}