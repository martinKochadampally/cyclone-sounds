package cycloneSounds.SocialHistory;

public class HistoryResponse {
    private String username;
    private String lengthInTime;

    public HistoryResponse(String username, String timeAgo) {
        this.username = username;
        this.lengthInTime = timeAgo;
    }

    public String getUsername() {
        return username;
    }
    public String getTimeAgo() {
        return lengthInTime;
    }
}