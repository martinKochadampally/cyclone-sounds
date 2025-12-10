package cycloneSounds.SocialHistory;

public class HistoryResponse {
    private String username;
    private String time;

    public HistoryResponse(String username, String time) {
        this.username = username;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }
    public String getTime() {

        return time;
    }
}