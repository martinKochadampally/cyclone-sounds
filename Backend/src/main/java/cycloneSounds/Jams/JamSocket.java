package cycloneSounds.Jams;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import cycloneSounds.websocketChats.CustomSpringConfigurator;
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


@Component
@ServerEndpoint(value = "/websocket/jams/{jamName}/{username}", configurator = CustomSpringConfigurator.class)
public class JamSocket {

    private static final Map<Session, String> sessionJamMap = new Hashtable<>();
    private static final Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static final Map<String, Map<Session, String>> jamSessions = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(JamSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("jamName") String jamName, @PathParam("username") String username) throws IOException {
        if (jamName == null || jamName.isEmpty() || username == null || username.isEmpty()) {
            session.close(new jakarta.websocket.CloseReason(
                    jakarta.websocket.CloseReason.CloseCodes.VIOLATED_POLICY, "Jam name and username are required"));
            return;
        }

        logger.info("User " + username + " connected to jam: " + jamName);

        sessionJamMap.put(session, jamName);
        sessionUsernameMap.put(session, username);

        jamSessions.putIfAbsent(jamName, new ConcurrentHashMap<>());
        jamSessions.get(jamName).put(session, username);

        try {
            JamRepository jamRepository = SpringContext.getBean(JamRepository.class);
            addMemberToJam(jamRepository, jamName, username);

            JamMessageRepository jamMessageRepository = SpringContext.getBean(JamMessageRepository.class);
            sendMessageToUser(session, getChatHistory(jamMessageRepository, jamName));

            broadcastToJam(jamName, username + " has joined the jam!");
        } catch (Exception e) {
            logger.error("Error on opening WebSocket for user " + username, e);
            session.close(new jakarta.websocket.CloseReason(jakarta.websocket.CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        logger.info("Received message: " + message);
        String jamName = sessionJamMap.get(session);
        String username = sessionUsernameMap.get(session);
        if (username == null || jamName == null) {
            logger.warn("Received message from unknown session");
            return;
        }

        try {
            JamMessageRepository jamMessageRepository = SpringContext.getBean(JamMessageRepository.class);

            // Assuming message is plain text you want to save and broadcast, or customize as per your model
            JamRepository jamRepository = SpringContext.getBean(JamRepository.class);
            Optional<Jam> jamOpt = jamRepository.findById(jamName);
            if (jamOpt.isEmpty()) {
                logger.warn("Jam not found: " + jamName);
                return;
            }
            Jam jam = jamOpt.get();

            // Create and save message entity
            JamMessage jamMessage = new JamMessage(username, jam, message);
            jamMessageRepository.save(jamMessage);

            // Broadcast message to all sessions in this jam
            broadcastToJam(jamName, username + ": " + message);
        } catch (Exception e) {
            logger.error("Failed to process message", e);
            session.getBasicRemote().sendText("{\"error\":\"Failed to send message: " + e.getMessage() + "\"}");
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String username = sessionUsernameMap.get(session);
        String jamName = sessionJamMap.get(session);

        if (username != null && jamName != null) {
            logger.info("User " + username + " disconnected from jam: " + jamName);

            try {
                JamRepository jamRepository = SpringContext.getBean(JamRepository.class);
                removeMemberFromJam(jamRepository, jamName, username);

                broadcastToJam(jamName, username + " has left the jam.");
            } catch (Exception e) {
                logger.error("Error during WebSocket close handling for user " + username, e);
                // Allow disconnect to proceed despite error
            }
        }

        sessionUsernameMap.remove(session);
        sessionJamMap.remove(session);

        if (jamName != null) {
            Map<Session, String> users = jamSessions.get(jamName);
            if (users != null) {
                users.remove(session);
                if (users.isEmpty()) {
                    jamSessions.remove(jamName);
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error for session " + session.getId(), throwable);
        try {
            session.close(new jakarta.websocket.CloseReason(
                    jakarta.websocket.CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
        } catch (IOException e) {
            logger.error("Failed to close session on error", e);
        }
    }

    private void broadcastToJam(String jamName, String message) {
        Map<Session, String> users = jamSessions.get(jamName);
        if (users != null) {
            users.keySet().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(message);
                    }
                } catch (IOException e) {
                    logger.error("Error sending message", e);
                }
            });
        }
    }

    private void sendMessageToUser(Session session, String message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            logger.error("Error sending private message", e);
        }
    }

    private void addMemberToJam(JamRepository jamRepository, String jamName, String username) {
        var jamOpt = jamRepository.findByIdWithMembers(jamName);
        jamOpt.ifPresent(jam -> {
            var members = jam.getMembers();
            if (!members.contains(username)) {
                members.add(username);
                jam.setMembers(members);
                jamRepository.save(jam);
            }
        });
    }

    private void removeMemberFromJam(JamRepository jamRepository, String jamName, String username) {
        var jamOpt = jamRepository.findByIdWithMembers(jamName);
        jamOpt.ifPresent(jam -> {
            var members = jam.getMembers();
            if (members.contains(username)) {
                members.remove(username);
                jam.setMembers(members);
                jamRepository.save(jam);
            }
        });
    }

    private String getChatHistory(JamMessageRepository jamMessageRepository, String jamName) {
        var messages = jamMessageRepository.findByJam_NameOrderBySentAsc(jamName);
        StringBuilder sb = new StringBuilder();
        if (messages != null && !messages.isEmpty()) {
            for (JamMessage message : messages) {
                sb.append(message.getUserName()).append(": ").append(message.getContent()).append("\n");
            }
        }
        return sb.toString();
    }
}
