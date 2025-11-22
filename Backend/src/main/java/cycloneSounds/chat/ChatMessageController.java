package cycloneSounds.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * Retrieves the chat message history exchanged between two users.
     *
     * @param user1 First user's username
     * @param user2 Second user's username
     * @return HTTP response containing the chat history as a list of ChatMessage objects
     */
    @Operation(
            summary = "Get chat history between two users",
            description = "Fetches all chat messages exchanged between the specified users in conversation order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved chat history"),
            @ApiResponse(responseCode = "404", description = "No chat history found for the specified users")
    })
    @GetMapping("/history/{user1}/{user2}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable String user1,
            @PathVariable String user2) {

        List<ChatMessage> chatHistory = chatMessageService.getChatHistory(user1, user2);
        return ResponseEntity.ok(chatHistory);
    }
}