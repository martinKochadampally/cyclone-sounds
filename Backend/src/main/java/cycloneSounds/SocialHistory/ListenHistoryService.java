package cycloneSounds.SocialHistory;

import cycloneSounds.Credentials.Credentials;
import cycloneSounds.Credentials.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListenHistoryService {

    @Autowired
    private ListenHistoryRepository historyRepo;

    @Autowired
    private CredentialRepository credentialRepo;

    public void recordListen(String username, String songId) {
        Credentials creds = credentialRepo.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ListenHistory history = new ListenHistory(creds, songId);
        historyRepo.save(history);
    }


    public List<HistoryResponse> getHistoryForSong(String songId) {
        List<ListenHistory> rawHistory = historyRepo.findTop10BySongIdOrderByListenedAtDesc(songId);

        return rawHistory.stream()
                .map(h -> new HistoryResponse(
                        h.getCredentials().getUsername(),
                        calculateTime(h.getListenedAt())
                ))
                .collect(Collectors.toList());
    }


    private String calculateTime(LocalDateTime pastTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(pastTime, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) return "Just now";
        if (seconds < 3600) return (seconds / 60) + " mins ago";
        if (seconds < 86400) return (seconds / 3600) + " hours ago";
        return (seconds / 86400) + " days ago";
    }
}