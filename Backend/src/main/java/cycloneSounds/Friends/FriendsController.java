package cycloneSounds.Friends;

import cycloneSounds.profilePage.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("friends")
public class FriendsController {

    private final FriendsService friendsService;

    @Autowired
    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @PostMapping("/request")
    public ResponseEntity<Friends> sendFriendRequest(@RequestBody Map<String, String> payload) {
        String requesterUsername = payload.get("requester");
        String receiverUsername = payload.get("receiver");

        try {
            Friends newRequest = friendsService.sendFriendRequest(requesterUsername, receiverUsername);
            return ResponseEntity.ok(newRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/respond/{requestId}")
    public ResponseEntity<Friends> respondToRequest(@PathVariable String     requestId, @RequestBody Map<String, String> payload) {
        String statusString = payload.get("status");
        Friends.Status status = Friends.Status.valueOf(statusString.toUpperCase());

        try {
            Friends updatedRequest = friendsService.respondToRequest(requestId, status);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/accepted/{username}")
    public ResponseEntity<List<Profile>> getAcceptedFriends(@PathVariable String username) {
        List<Profile> friends = friendsService.getAcceptedFriends(username);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/pending/{username}")
    public ResponseEntity<List<Friends>> getPendingRequests(@PathVariable String username) {
        List<Friends> requests = friendsService.getPendingRequests(username);
        return ResponseEntity.ok(requests);
    }
}