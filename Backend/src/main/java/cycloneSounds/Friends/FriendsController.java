package cycloneSounds.Friends;

import cycloneSounds.profilePage.Profile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendsService friendsService;

    @Autowired
    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    /**
     * Sends a friend request from one user to another.
     *
     * @param payload JSON map containing "requester" and "receiver" usernames
     * @return ResponseEntity containing the created Friends object or bad request status
     */
    @Operation(summary = "Send a friend request",
            description = "Creates a friend request from requester to receiver.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or error creating request")
    })
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

    /**
     * Responds to a friend request by setting its status (e.g., accepted, rejected).
     *
     * @param requestId ID of the friend request
     * @param payload JSON map containing the new status under key "status"
     * @return ResponseEntity containing updated Friends object or not found status
     */
    @Operation(summary = "Respond to a friend request",
            description = "Updates the status of a friend request identified by requestId.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request status updated"),
            @ApiResponse(responseCode = "404", description = "Friend request not found")
    })
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

    /**
     * Retrieves a list of accepted friends for the specified username.
     *
     * @param username Username whose accepted friends to retrieve
     * @return ResponseEntity containing list of Profile objects of accepted friends
     */
    @Operation(summary = "Get accepted friends",
            description = "Retrieves all friends who have accepted friendship with the specified user.")
    @ApiResponse(responseCode = "200", description = "List of accepted friends retrieved")
    @GetMapping("/accepted/{username}")
    public ResponseEntity<List<Profile>> getAcceptedFriends(@PathVariable String username) {
        List<Profile> friends = friendsService.getAcceptedFriends(username);
        return ResponseEntity.ok(friends);
    }

    /**
     * Retrieves a list of pending friend requests received by the specified username.
     *
     * @param username Username whose pending friend requests to retrieve
     * @return ResponseEntity containing list of Friends objects representing pending requests
     */
    @Operation(summary = "Get pending friend requests",
            description = "Retrieves all pending friend requests received by the specified user.")
    @ApiResponse(responseCode = "200", description = "List of pending friend requests retrieved")
    @GetMapping("/pending/{username}")
    public ResponseEntity<List<Friends>> getPendingRequests(@PathVariable String username) {
        List<Friends> requests = friendsService.getPendingRequests(username);
        return ResponseEntity.ok(requests);
    }
}