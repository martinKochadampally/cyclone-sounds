package cycloneSounds.SocialHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller that also uses RESTful API to manage teh user listening history.  ListenHistoryController
 * handles recording the time a user listens to songs and can grab the hisotrical listening data for songs.
 * @author mark Seward
 */
@RestController
@RequestMapping("/history")
public class ListenHistoryController {

    @Autowired
    private ListenHistoryService historyService;

    /**
     * Records a new listen event for a user. This method will be called when a user passes the listening threshold
     * and logs the timestamp as well as links the user to the song.
     * @param username
     * @param songId
     * @return string with a "success" or "failure" message
     */
    @Operation(summary = "Record a listening event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listen recorded successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error or invalid data")
    })
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

    /**
     * Gets the listening history for a song. Allows the timetabl of who listened to a song  and when.
     * @param songId
     * @return list of HistoryResponse objects with user and timestamp variables
     */
    @Operation(summary = "Get listening history for a song")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of history records retrieved")
    })
    @GetMapping("/song/{songId}")
    public List<HistoryResponse> getSongHistory(@PathVariable String songId)
    {
        return historyService.getHistoryForSong(songId);
    }
}