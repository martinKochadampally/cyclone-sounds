package cycloneSounds;

import cycloneSounds.chat.ChatMessage;
import cycloneSounds.chat.ChatMessageRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main.class)
public class MarkSystemTest {

    @LocalServerPort
    int port;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    public void createHelperUser(String username, String name) {
        String jsonBody = "{\"username\":\"" + username + "\", \"name\":\"" + name + "\", \"favArtist\":\"A\", \"favGenre\":\"G\", \"favSong\":\"S\", \"biography\":\"B\"}";

        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .post("/profiles");
    }

    //Tests for profilePage

    @Test
    public void createProfileCheck() {
        String jsonBody = "{" +
                "\"username\": \"TestUser1\"," +
                "\"name\": \"Mark Tester\"," +
                "\"favArtist\": \"Coldplay\"," +
                "\"favGenre\": \"Pop\"," +
                "\"favSong\": \"Yellow\"," +
                "\"biography\": \"Just a test bio\"" +
                "}";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .when()
                .post("/profiles");

        assertEquals(200, response.getStatusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("success"));
    }

    @Test
    public void getAllProfilesCheck() {
        createHelperUser("ListUser1", "Lister");
        createHelperUser("ListUser2", "Lister");

        Response response = RestAssured.given()
                .when()
                .get("/profiles");

        assertEquals(200, response.getStatusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("ListUser1"));
        assertTrue(responseBody.contains("ListUser2"));
    }

    @Test
    public void updateProfileCheck() {
        String username = "UpdateUser";
        createHelperUser(username, "Original Name");

        String updateBody = "{" +
                "\"name\": \"New Name\"," +
                "\"favArtist\": \"New Artist\"," +
                "\"favGenre\": \"New Genre\"," +
                "\"favSong\": \"New Song\"," +
                "\"biography\": \"New Bio\"" +
                "}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/profiles/" + username);

        assertEquals(200, response.getStatusCode());

        RestAssured.given()
                .when()
                .get("/profiles/" + username)
                .then()
                .statusCode(200)
                .body("name", equalTo("New Name"));
    }

    @Test
    public void deleteProfileCheck() {
        String username = "DeleteMe";
        createHelperUser(username, "Temporary User");

        Response deleteResponse = RestAssured.given()
                .when()
                .delete("/profiles/" + username);

        assertEquals(200, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().asString().contains("success"));

        Response getResponse = RestAssured.given()
                .when()
                .get("/profiles/" + username);

        String responseBody = getResponse.getBody().asString();
        assertTrue(responseBody.isEmpty() || responseBody.equals(""));
    }

    //Review testing

    @Test
    public void postReviewCheck() {
        createHelperUser("MusicCritic", "Mark");

        Response response = RestAssured.given()
                .param("reviewer", "MusicCritic")
                .param("songName", "Bohemian Rhapsody")
                .param("artistName", "Queen")
                .param("rating", 5.0)
                .param("description", "Absolute masterpiece!")
                .when()
                .post("/review");

        assertEquals(200, response.getStatusCode());

        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("masterpiece"));
    }

    @Test
    public void getReviewsByReviewerCheck() {
        createHelperUser("SpecificReviewer", "Mark");

        RestAssured.given()
                .param("reviewer", "SpecificReviewer")
                .param("songName", "Test Song")
                .param("artistName", "Test Artist")
                .param("rating", 4.0)
                .param("description", "Pretty good")
                .post("/review");

        Response response = RestAssured.given()
                .when()
                .get("/review/username/SpecificReviewer");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Pretty good"));
    }

    @Test
    public void upvoteReviewCheck() {
        createHelperUser("VoterUser", "Mark");

        Response createResponse = RestAssured.given()
                .param("reviewer", "VoterUser")
                .param("songName", "Upvote Song")
                .param("artistName", "Artist")
                .param("rating", 3.0)
                .param("description", "Decent")
                .post("/review");

        int reviewId = createResponse.jsonPath().getInt("id");

        Response upvoteResponse = RestAssured.given()
                .when()
                .put("/review/upvote/" + reviewId);

        assertEquals(200, upvoteResponse.getStatusCode());
        assertTrue(upvoteResponse.getBody().asString().contains("upVotes") ||
                upvoteResponse.getBody().asString().contains("upvotes"));
    }

    @Test
    public void deleteReviewCheck() {
        createHelperUser("DeleteReviewer", "Mark");

        Response createResponse = RestAssured.given()
                .param("reviewer", "DeleteReviewer")
                .param("songName", "Delete Song")
                .param("artistName", "Artist")
                .param("rating", 1.0)
                .param("description", "Bad")
                .post("/review");

        int reviewId = createResponse.jsonPath().getInt("id");

        Response deleteResponse = RestAssured.given()
                .when()
                .delete("/review/" + reviewId);

        assertEquals(200, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().asString().contains("success"));
    }

    //Tests for playlists
    @Test
    public void createPlaylistCheck() {
        createHelperUser("PlaylistUser", "Mark");

        String uniqueName = "My Jams " + System.currentTimeMillis();

        Response response = RestAssured.given()
                .param("playlistName", uniqueName)
                .param("username", "PlaylistUser")
                .when()
                .post("/api/playlists/create");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains(uniqueName));
    }

    @Test
    public void addSongToPlaylistCheck() {
        createHelperUser("mgseward", "Mark");

        RestAssured.given()
                .param("playlistName", "Study")
                .param("username", "mgseward")
                .post("/api/playlists/create");

        String targetSpotifyId = "testSpotifyId123";

        if (songRepository.findBySpotifyId(targetSpotifyId).isEmpty()) {
            Song testSong = new Song("Runnin", "21 Savage, Metro Boomin");
            testSong.setSpotifyId(targetSpotifyId);
            songRepository.save(testSong);
        }

        Response response = RestAssured.given()
                .param("songName", "Runnin")
                .param("artist", "21 Savage, Metro Boomin")
                .when()
                .post("/api/playlists/mgseward/Study/add");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Runnin"));
    }

    @Test
    public void removeSongFromPlaylistCheck() {
        createHelperUser("RemoveUser", "Mark");

        long timestamp = System.currentTimeMillis();
        String uniquePlaylist = "RemoveList_" + timestamp;
        String uniqueSongName = "SongToRemove_" + timestamp;
        String uniqueSongId = "RemoveId_" + timestamp;

        RestAssured.given()
                .param("playlistName", uniquePlaylist)
                .param("username", "RemoveUser")
                .post("/api/playlists/create");

        Song s = new Song(uniqueSongName, "ArtistToRemove");
        s.setSpotifyId(uniqueSongId);
        songRepository.save(s);

        RestAssured.given()
                .param("songName", uniqueSongName)
                .param("artist", "ArtistToRemove")
                .post("/api/playlists/RemoveUser/" + uniquePlaylist + "/add");

        Response response = RestAssured.given()
                .param("songName", uniqueSongName)
                .param("artist", "ArtistToRemove")
                .when()
                .delete("/api/playlists/RemoveUser/" + uniquePlaylist + "/remove");

        assertEquals(200, response.getStatusCode());

        Response listResponse = RestAssured.given()
                .when()
                .get("/api/playlists/RemoveUser/" + uniquePlaylist + "/songs");

        String responseBody = listResponse.getBody().asString();
        assertEquals(200, listResponse.getStatusCode());
        assertTrue(!responseBody.contains(uniqueSongName));
    }

    @Test
    public void getPlaylistsByOwnerCheck() {
        createHelperUser("OwnerUser", "Mark");

        RestAssured.given()
                .param("playlistName", "List One")
                .param("username", "OwnerUser")
                .post("/api/playlists/create");

        RestAssured.given()
                .param("playlistName", "List Two")
                .param("username", "OwnerUser")
                .post("/api/playlists/create");

        Response response = RestAssured.given()
                .when()
                .get("/api/playlists/owner/OwnerUser");

        assertEquals(200, response.getStatusCode());
        String body = response.getBody().asString();
        assertTrue(body.contains("List One"));
        assertTrue(body.contains("List Two"));
    }

    @Test
    public void getSongsFromPlaylistCheck() {
        createHelperUser("SongsUser", "Mark");

        RestAssured.given()
                .param("playlistName", "SongList")
                .param("username", "SongsUser")
                .post("/api/playlists/create");

        String uniqueId = "UniqueId_" + System.currentTimeMillis();
        Song s = new Song("UniqueSong", "UniqueArtist");
        s.setSpotifyId(uniqueId);
        songRepository.save(s);

        RestAssured.given()
                .param("songName", "UniqueSong")
                .param("artist", "UniqueArtist")
                .post("/api/playlists/SongsUser/SongList/add");

        Response response = RestAssured.given()
                .when()
                .get("/api/playlists/SongsUser/SongList/songs");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("UniqueSong"));
    }

    @Test
    public void deletePlaylistCheck() {
        createHelperUser("DeletePlUser", "Mark");

        RestAssured.given()
                .param("playlistName", "DeleteMeList")
                .param("username", "DeletePlUser")
                .post("/api/playlists/create");

        Response response = RestAssured.given()
                .when()
                .delete("/api/playlists/DeletePlUser/DeleteMeList");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Playlist deleted"));
    }

    @Test
    public void spotifySearchCheck() {
        Response response = RestAssured.given()
                .param("query", "Taylor Swift")
                .when()
                .post("/api/songs/search");

        assertEquals(200, response.getStatusCode());

        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("Search complete") || responseBody.contains("Error"));
    }

    //Testing friends code

    @Test
    public void sendFriendRequestCheck() {
        long timestamp = System.currentTimeMillis();
        String requester = "Requester_" + timestamp;
        String receiver = "Receiver_" + timestamp;

        createHelperUser(requester, "Req Name");
        createHelperUser(receiver, "Rec Name");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("requester", requester);
        requestBody.put("receiver", receiver);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/friends/request");

        assertEquals(200, response.getStatusCode());

        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains(requester));
        assertTrue(responseBody.contains(receiver));
    }

    @Test
    public void respondToFriendRequestCheck() {
        long timestamp = System.currentTimeMillis();
        String requester = "ReqResp_" + timestamp;
        String receiver = "RecResp_" + timestamp;

        createHelperUser(requester, "R1");
        createHelperUser(receiver, "R2");

        Map<String, String> reqBody = new HashMap<>();
        reqBody.put("requester", requester);
        reqBody.put("receiver", receiver);

        Response friendRequest = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(reqBody)
                .post("/api/friends/request");

        String requestId = friendRequest.jsonPath().getString("id");

        Map<String, String> respBody = new HashMap<>();
        respBody.put("status", "ACCEPTED");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(respBody)
                .when()
                .post("/api/friends/respond/" + requestId);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("ACCEPTED"));
    }

    @Test
    public void getPendingRequestsCheck() {
        long timestamp = System.currentTimeMillis();
        String requester = "PendingReq_" + timestamp;
        String receiver = "PendingRec_" + timestamp;

        createHelperUser(requester, "R1");
        createHelperUser(receiver, "R2");


        Map<String, String> reqBody = new HashMap<>();
        reqBody.put("requester", requester);
        reqBody.put("receiver", receiver);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(reqBody)
                .post("/api/friends/request");

        Response response = RestAssured.given()
                .when()
                .get("/api/friends/pending/" + receiver);

        assertEquals(200, response.getStatusCode());

        String body = response.getBody().asString();
        assertTrue(body.contains(requester));
    }

    @Test
    public void getAcceptedFriendsCheck() {
        long timestamp = System.currentTimeMillis();
        String userA = "FriendA_" + timestamp;
        String userB = "FriendB_" + timestamp;

        createHelperUser(userA, "Alice");
        createHelperUser(userB, "Bob");

        Map<String, String> reqBody = new HashMap<>();
        reqBody.put("requester", userA);
        reqBody.put("receiver", userB);

        Response requestResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(reqBody)
                .post("/api/friends/request");

        String requestId = requestResponse.jsonPath().getString("id");

        Map<String, String> respBody = new HashMap<>();
        respBody.put("status", "ACCEPTED");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(respBody)
                .post("/api/friends/respond/" + requestId);

        Response response = RestAssured.given()
                .when()
                .get("/api/friends/accepted/" + userA);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains(userB));
    }

    //Testing DM features
    @Test
    public void getChatHistoryCheck() {
        long timestamp = System.currentTimeMillis();
        String userA = "ChatUserA_" + timestamp;
        String userB = "ChatUserB_" + timestamp;

        createHelperUser(userA, "Martin");
        createHelperUser(userB, "Alex");


        ChatMessage msg1 = new ChatMessage(userA, userB, "yo Alex");
        chatMessageRepository.save(msg1);

        ChatMessage msg2 = new ChatMessage(userB, userA, "how is code going?");
        chatMessageRepository.save(msg2);

        Response response = RestAssured.given()
                .when()
                .get("/api/chat/history/" + userA + "/" + userB);

        assertEquals(200, response.getStatusCode());

        String responseBody = response.getBody().asString();

        assertTrue(responseBody.contains("yo Alex"));
        assertTrue(responseBody.contains("how is code going?"));
    }
}