package cycloneSounds.Jams;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/websocket/jams/{jamName}/{username}")
public class JamSocket {

    private static JamRepository jamRepository;
    private static JamMessageRepository jamMessageRepository;  // Add this field

    @Autowired
    public void setJamsRepository(JamRepository repo) {
        jamRepository = repo;
    }

    @Autowired
    public void setJamMessageRepository(JamMessageRepository repo) {
        jamMessageRepository = repo;
    }

    private static Map<Session, String> sessionJamMap = new Hashtable<>();
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Map<Session, String>> jamSessions = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(JamSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("jamName") String jamName, @PathParam("username") String username) throws IOException {
        logger.info("User " + username + " connected to jam: " + jamName);

        sessionJamMap.put(session, jamName);
        sessionUsernameMap.put(session, username);

        jamSessions.putIfAbsent(jamName, new ConcurrentHashMap<>());
        jamSessions.get(jamName).put(session, username);

        // Add user to Jams entity members list
        addMemberToJam(jamName, username);

        // Send chat history to the newly connected user
        sendMessageToUser(session, getChatHistory(jamName));

        // Broadcast join message to others in jam
        broadcastToJam(jamName, username + " has joined the jam!");
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String jamName = sessionJamMap.get(session);
        String username = sessionUsernameMap.get(session);
        String messageContent = message;

        // Load Jam entity by jamName from repository
        Optional<Jam> optionalJam = jamRepository.findById(jamName);
        if (optionalJam.isEmpty()) {
            // Handle error: jam not found, reject or log message
            logger.error("Jam not found: " + jamName);
            return;
        }

        Jam jam = optionalJam.get();

        // Create JamMessage entity with Jam object
        JamMessage jamMessage = new JamMessage(username, jam, messageContent);
        jamMessageRepository.save(jamMessage);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String jamName = sessionJamMap.get(session);
        String username = sessionUsernameMap.get(session);
        logger.info("User " + username + " disconnected from jam: " + jamName);

        sessionJamMap.remove(session);
        sessionUsernameMap.remove(session);

        if (jamName != null) {
            Map<Session, String> users = jamSessions.get(jamName);
            if (users != null) {
                users.remove(session);
                if (users.isEmpty()) {
                    jamSessions.remove(jamName);
                }
            }
            removeMemberFromJam(jamName, username);
            broadcastToJam(jamName, username + " has left the jam.");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error", throwable);
    }

    private void broadcastToJam(String jamName, String message) {
        Map<Session, String> users = jamSessions.get(jamName);
        if (users != null) {
            users.keySet().forEach(session -> {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("Error sending message", e);
                }
            });
        }
    }

    private void sendMessageToUser(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.error("Error sending private message", e);
        }
    }

    private void addMemberToJam(String jamName, String username) {
        var jamOpt = jamRepository.findById(jamName);
        jamOpt.ifPresent(jam -> {
            var members = jam.getMembers();
            if (!members.contains(username)) {
                members.add(username);
                jam.setMembers(members);
                jamRepository.save(jam);
            }
        });
    }

    private void removeMemberFromJam(String jamName, String username) {
        var jamOpt = jamRepository.findById(jamName);
        jamOpt.ifPresent(jam -> {
            var members = jam.getMembers();
            if (members.contains(username)) {
                members.remove(username);
                jam.setMembers(members);
                jamRepository.save(jam);
            }
        });
    }

    private String getChatHistory(String jamName) {
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