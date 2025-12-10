package cycloneSounds.SocialHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/history")
public class ListenHistoryController {

    @Autowired
    private ListenHistoryService historyService;

    @PostMapping("/record")
    public String recordListen(@RequestParam String username, @RequestParam String songId)
    {
        try {
            historyService.recordListen(username, songId);
            return "{\"message\":\"success\"}";
        } catch (Exception e) {
            return "{\"message\":\"failure\", \"error\":\"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping("/song/{songId}")
    public List<HistoryResponse> getSongHistory(@PathVariable String songId)
    {
        return historyService.getHistoryForSong(songId);
    }
}