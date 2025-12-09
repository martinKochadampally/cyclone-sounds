package cycloneSounds.Jams;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.Vote.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.json.JSONObject;
import org.json.JSONException;


@Component
@ServerEndpoint(value = "/websocket/jams/{jamName}/{username}", configurator = CustomSpringConfigurator.class)
public class JamSocket {

    private static JamRepository jamRepository;
    private static VoteService voteService;
    private static VoteRepository voteRepository;
    private static UserVoteRepository userVoteRepository;
    private static SongRepository songRepository;

    private static final Map<Session, String> sessionJamMap = new Hashtable<>();
    private static final Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static final Map<String, Map<Session, String>> jamSessions = new ConcurrentHashMap<>();
    // <Jam Name, <Song Id, <Username, Vote>>>
    private static final Map<String, Map<Integer, Map<String, String>>> jamVotes = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(JamSocket.class);

    @Autowired
    public void setJamRepository(JamRepository repository) {
        JamSocket.jamRepository = repository;
    }

    @Autowired
    public void setVoteService(VoteService service) {
        JamSocket.voteService = service;
    }

    @Autowired
    public void setVoteRepository(VoteRepository repository) {
        JamSocket.voteRepository = repository;
    }

    @Autowired
    public void setUserVoteRepository(UserVoteRepository repository) {
        JamSocket.userVoteRepository = repository;
    }

    @Autowired
    public void setSongRepository(SongRepository repository) {
        JamSocket.songRepository = repository;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("jamName") String jamName, @PathParam("username") String username) throws IOException {
        if (jamName == null || jamName.isEmpty() || username == null || username.isEmpty()) {
            session.close(new jakarta.websocket.CloseReason(
                    jakarta.websocket.CloseReason.CloseCodes.VIOLATED_POLICY, "Jam name and username are required"));
            return;
        }

        logger.info("========== USER CONNECTING ==========");
        logger.info("User: " + username);
        logger.info("Jam: " + jamName);
        logger.info("Session ID: " + session.getId());

        sessionJamMap.put(session, jamName);
        sessionUsernameMap.put(session, username);

        jamSessions.putIfAbsent(jamName, new ConcurrentHashMap<>());
        jamSessions.get(jamName).put(session, username);

        logger.info("Total users in jam '" + jamName + "': " + jamSessions.get(jamName).size());
        logger.info("All users in jam: " + jamSessions.get(jamName).values());

        try {
            JamRepository jamRepository = SpringContext.getBean(JamRepository.class);
            addMemberToJam(jamRepository, jamName, username);

            JamMessageRepository jamMessageRepository = SpringContext.getBean(JamMessageRepository.class);
            String history = getChatHistory(jamMessageRepository, jamName);
            logger.info("Sending chat history (" + history.length() + " characters)");
            sendMessageToUser(session, history);

            logger.info("Broadcasting join message...");
            broadcastToJam(jamName, username + " has joined the jam!");
        } catch (Exception e) {
            logger.error("Error on opening WebSocket for user " + username, e);
            session.close(new jakarta.websocket.CloseReason(jakarta.websocket.CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
        }

        logger.info("========== USER CONNECTED SUCCESSFULLY ==========");
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
            if (message.trim().startsWith("{")) {
                JSONObject messageJson = new JSONObject(message);
                String type = messageJson.optString("type", "chat");

                if ("song_suggestion".equals(type)) {
                    String adminUsername = messageJson.getString("receiver");
                    broadcastToSpecificUser(jamName, adminUsername, message);
                    return;
                }
                else if ("chat".equals(type)) {
                    message = messageJson.getString("content");
                }
                else if ("song_vote_request".equals(type)) {
                    Integer songId = messageJson.getInt("songId");
                    String suggester = messageJson.getString("suggester");

                    jamVotes.putIfAbsent(jamName, new ConcurrentHashMap<>());
                    Map<Integer, Map<String, String>> voteSessions = jamVotes.get(jamName);

                    voteSessions.putIfAbsent(songId, new ConcurrentHashMap<>());
                    Map<String, String> currentVotes = voteSessions.get(songId);
                    currentVotes.put(suggester, "yes");// Suggester automatically votes yes

                    Jam jam = jamRepository.findById(jamName).orElse(null);
                    Song song = songRepository.findById(songId).orElse(null);

                    Vote vote = new Vote(jam, song, suggester);
                    voteRepository.save(vote);

                    int voteId = vote.getVoteId();
                    voteService.recordVoteAsync(voteId, suggester, "yes");

                    JSONObject broadcastJson = new JSONObject();
                    broadcastJson.put("type", "song_vote_request");
                    broadcastJson.put("voteId", voteId);
                    broadcastJson.put("songId", songId);
                    broadcastJson.put("suggester", suggester);

                    broadcastToJam(jamName, broadcastJson.toString());
                }
                else if ("song_vote".equals(type)) {
                    Integer songId = messageJson.getInt("songId");
                    String voter = messageJson.getString("voter");
                    String userVote = messageJson.getString("vote"); // "yes" or "no"

                    int voteId = voteRepository.findByJam_NameAndSong_SongId(jamName, songId)
                            .map(Vote::getVoteId)
                            .orElse(-1);


                    jamVotes.putIfAbsent(jamName, new ConcurrentHashMap<>());
                    Map<Integer, Map<String, String>> songsInJam = jamVotes.get(jamName);

                    songsInJam.putIfAbsent(songId, new ConcurrentHashMap<>());
                    Map<String, String> currentVotes = songsInJam.get(songId);


                    currentVotes.put(voter, userVote);
                    if (voteId != -1) {
                        voteService.recordVoteAsync(voteId, voter, userVote);
                    }

                    checkVoteResult(jamName, songId, currentVotes);
                }
            }

            JamMessageRepository jamMessageRepository = SpringContext.getBean(JamMessageRepository.class);
            JamRepository jamRepository = SpringContext.getBean(JamRepository.class);
            Optional<Jam> jamOpt = jamRepository.findById(jamName);
            if (jamOpt.isEmpty()) {
                logger.warn("Jam not found: " + jamName);
                return;
            }
            Jam jam = jamOpt.get();

            JamMessage jamMessage = new JamMessage(username, jam, message);
            jamMessageRepository.save(jamMessage);

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

    private void checkVoteResult(String jamName, int songId, Map<String, String> votes) {
        int totalUsers = jamSessions.get(jamName).size();
        long yesVotes = votes.values().stream().filter(v -> "yes".equals(v)).count();
        long noVotes = votes.values().stream().filter(v -> "no".equals(v)).count();

        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("type", "vote_result");
            resultJson.put("song", songId);

            if (yesVotes > totalUsers / 2.0) {
                resultJson.put("result", "approved");
                broadcastToJam(jamName, resultJson.toString());
                votes.clear();
            } else if (noVotes >= totalUsers / 2.0) {
                resultJson.put("result", "denied");
                broadcastToJam(jamName, resultJson.toString());
                votes.clear();
            }
        } catch (JSONException e) {
            logger.error("Error creating vote result JSON", e);
        }
    }

    private void broadcastToJam(String jamName, String message) {
        logger.info("========== BROADCAST START ==========");
        logger.info("Jam name: " + jamName);
        logger.info("Message: " + message);

        Map<Session, String> users = jamSessions.get(jamName);

        if (users == null) {
            logger.error("No user map found for jam: " + jamName);
            logger.info("Available jams: " + jamSessions.keySet());
            return;
        }

        logger.info("Found " + users.size() + " users in jam");
        logger.info("Users: " + users.values());

        users.forEach((session, username) -> {
            try {
                logger.info("Attempting to send to user: " + username + " (session open: " + session.isOpen() + ")");
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                    logger.info("Successfully sent to: " + username);
                } else {
                    logger.warn("Session closed for user: " + username);
                }
            } catch (IOException e) {
                logger.error("Error sending message to " + username, e);
            }
        });

        logger.info("========== BROADCAST END ==========");
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

    private void broadcastToSpecificUser(String jamName, String targetUsername, String message) {
        Map<Session, String> users = jamSessions.get(jamName);
        if (users != null) {
            users.forEach((session, username) -> {
                if (username.equals(targetUsername)) {
                    try {
                        if (session.isOpen()) {
                            session.getBasicRemote().sendText(message);
                        }
                    } catch (IOException e) {
                        logger.error("Error sending message to specific user", e);
                    }
                }
            });
        }
    }
}