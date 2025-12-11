package cycloneSounds;

// Import Local classes
import cycloneSounds.Credentials.CredentialRepository;
import cycloneSounds.Jams.JamMessageRepository;
import cycloneSounds.Jams.JamRepository;
import cycloneSounds.Playlists.Playlist;
import cycloneSounds.Playlists.PlaylistRepository;
import cycloneSounds.Reviews.ReviewRepository;
import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;
import cycloneSounds.Vote.UserVote;
import cycloneSounds.Vote.UserVoteRepository;
import cycloneSounds.Vote.Vote;
import cycloneSounds.Vote.VoteRepository;
import cycloneSounds.Jams.Jam;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class MartinSystemTest {
    private Song song1;
    private Song song2;

    private static final String BASE_PATH = "/blind-review";
    private static final String TEST_USER1 = "user1";
    private static final String TEST_USER2 = "user2";

    private static final String JAM_MANAGER = "manager";
    private static final String REGULAR_USER = "user";
    private static final String ADMIN_USER = "admin";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_JAM_NAME = "TestJam";
    private static final String APPROVAL_VOTING = "Voting";

    @Autowired
    private JamRepository jamRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private JamMessageRepository jamMessageRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserVoteRepository userVoteRepository;




    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Before
    public void cleanDatabase() {
        jamMessageRepository.deleteAllInBatch();
        userVoteRepository.deleteAllInBatch();
        voteRepository.deleteAllInBatch();
        jamRepository.deleteAllInBatch();
        credentialRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        songRepository.deleteAllInBatch();
        profileRepository.deleteAllInBatch();
        playlistRepository.deleteAllInBatch();

        song1 = new Song("Song1", "Artist1");
        song1.setSpotifyId("id1");
        song1 = songRepository.save(song1);

        song2 = new Song("Song1", "Artist2");
        song2.setSpotifyId("id2");
        song2 = songRepository.save(song2);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                      "username": "%s",
                      "songId": %d,
                      "rating": 5,
                      "reviewText": "loved this one."
                    }
                    """, TEST_USER2, song1.getSongId()))
                .when()
                .post(BASE_PATH + "/review")
                .then()
                .statusCode(200);
    }

    /**
     * Helper method to create a user and assert success (200).
     */
    private void createCredential(String username, String password, String accountType) {
        RestAssured.given()
                .param("username", username)
                .param("password", password)
                .param("accountType", accountType)
                .when()
                .post("/credentials")
                .then()
                .statusCode(200);
    }

    /**
     * Helper method to create a jam and assert success (200).
     */
    private void createJam(String managerUsername, String jamName, String approvalType) {
        RestAssured.given()
                .pathParam("username", managerUsername)
                .pathParam("jamName", jamName)
                .pathParam("approvalType", approvalType)
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then()
                .statusCode(200);
    }

    // ===============================================POST==============================================================


    @Test
    public void createJamByJamManager() {
        createCredential(JAM_MANAGER, TEST_PASSWORD, "jamManager");

        RestAssured.given()
                .pathParam("username", JAM_MANAGER)
                .pathParam("jamName", TEST_JAM_NAME)
                .pathParam("approvalType", APPROVAL_VOTING)
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then()
                .statusCode(200)
                .body("name", equalTo(TEST_JAM_NAME))
                .body("manager", equalTo(JAM_MANAGER))
                .body("approvalType", equalTo(APPROVAL_VOTING))
                .body("members", hasItem(JAM_MANAGER));
    }

    @Test
    public void createJamByAdmin() {
        createCredential(ADMIN_USER, TEST_PASSWORD, "admin");

        RestAssured.given()
                .pathParam("username", ADMIN_USER)
                .pathParam("jamName", TEST_JAM_NAME)
                .pathParam("approvalType", "auto")
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then()
                .statusCode(200)
                .body("manager", equalTo(ADMIN_USER));
    }

    @Test
    public void createJamWithRegularUser() {
        createCredential(REGULAR_USER, TEST_PASSWORD, "regular");

        RestAssured.given()
                .pathParam("username", REGULAR_USER)
                .pathParam("jamName", TEST_JAM_NAME)
                .pathParam("approvalType", APPROVAL_VOTING)
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then()
                .statusCode(403)
                .body(emptyString());
    }

    @Test
    public void createJamWithUserMissing() {
        RestAssured.given()
                .pathParam("username", "missingUser")
                .pathParam("jamName", TEST_JAM_NAME)
                .pathParam("approvalType", APPROVAL_VOTING)
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then()
                .statusCode(403);
    }

    @Test
    public void createJamWithDuplicateName() {
        createCredential(JAM_MANAGER, TEST_PASSWORD, "jamManager");
        createJam(JAM_MANAGER, "DuplicateJamName", APPROVAL_VOTING);

        RestAssured.given()
                .pathParam("username", JAM_MANAGER)
                .pathParam("jamName", "DuplicateJamName")
                .pathParam("approvalType", APPROVAL_VOTING)
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then()
                .statusCode(400)
                .body(emptyString());
    }

    // ============================================ GET ALL ============================================================

    @Test
    public void getAllJams() {
        createCredential(JAM_MANAGER, TEST_PASSWORD, "jamManager");
        createJam(JAM_MANAGER, "JamA", "manual");
        createJam(JAM_MANAGER, "JamB", "auto");

        RestAssured.given()
                .when()
                .get("/api/jams")
                .then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("name", hasItems("JamA", "JamB"));
    }

    // ============================================ GET ONE ============================================================

    @Test
    public void getJamByName() {
        createCredential(JAM_MANAGER, TEST_PASSWORD, "jamManager");
        createJam(JAM_MANAGER, TEST_JAM_NAME, APPROVAL_VOTING);

        RestAssured.given()
                .pathParam("name", TEST_JAM_NAME)
                .when()
                .get("/api/jams/{name}")
                .then()
                .statusCode(200)
                .body("name", equalTo(TEST_JAM_NAME))
                .body("manager", equalTo(JAM_MANAGER));
    }

    @Test
    public void getNonExistentJam() {
        RestAssured.given()
                .pathParam("name", "NonExistentJam")
                .when()
                .get("/api/jams/{name}")
                .then()
                .statusCode(404)
                .body(emptyString());
    }

    // ============================================ DELETE =============================================================

    @Test
    public void deleteJamByName() {
        createCredential(JAM_MANAGER, TEST_PASSWORD, "jamManager");
        createJam(JAM_MANAGER, TEST_JAM_NAME, APPROVAL_VOTING);

        RestAssured.given()
                .pathParam("name", TEST_JAM_NAME)
                .when()
                .delete("/api/jams/{name}")
                .then()
                .statusCode(204)
                .body(emptyString());

        RestAssured.given()
                .pathParam("name", TEST_JAM_NAME)
                .when()
                .get("/api/jams/{name}")
                .then()
                .statusCode(404);
    }

    @Test
    public void deleteNonExistentJam() {
        RestAssured.given()
                .pathParam("name", "NonExistentJam")
                .when()
                .delete("/api/jams/{name}")
                .then()
                .statusCode(404);
    }

    // ========================================= GET CHAT HISTORY ======================================================

    @Test
    public void getChatHistoryNonExistentJam() {
        RestAssured.given()
                .pathParam("jamName", "UnknownJam")
                .when()
                .get("/api/jams/chatHistory/{jamName}")
                .then()
                .statusCode(200)
                .body("", hasSize(0));
    }

    // ============================================ GET NEXT BLIND SONG ================================================

    @Test
    public void getNextBlindSongExcludesReviewed() {
        RestAssured.given()
                .param("username", TEST_USER2)
                .when()
                .get(BASE_PATH + "/next")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("songId", equalTo(song2.getSongId()))
                .body("songName", equalTo(song2.getSongName()));
    }

    @Test
    public void getNextBlindSongNoSongsRemaining() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\", \"songId\": %d, \"rating\": 1, \"reviewText\": \"T1\"}",
                        TEST_USER1, song1.getSongId()))
                .post(BASE_PATH + "/review");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"username\": \"%s\", \"songId\": %d, \"rating\": 1, \"reviewText\": \"T2\"}",
                        TEST_USER1, song2.getSongId()))
                .post(BASE_PATH + "/review");

        RestAssured.given()
                .param("username", TEST_USER1)
                .when()
                .get(BASE_PATH + "/next")
                .then()
                .statusCode(204)
                .body(emptyString());
    }

    @Test
    public void getNextBlindSongMissingUsername() {
        RestAssured.given()
                .when()
                .get(BASE_PATH + "/next")
                .then()
                .statusCode(400);
    }

    // ============================================ SUBMIT BLIND REVIEW ================================================

    @Test
    public void submitBlindReview() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                      "username": "%s",
                      "songId": %d,
                      "rating": 4.5,
                      "reviewText": "Great track, love the chorus and production."
                    }
                    """, TEST_USER1, song2.getSongId()))
                .when()
                .post(BASE_PATH + "/review")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("message", equalTo("success"));
    }

    @Test
    public void submitBlindReviewNonExistentSong() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                      "username": "%s",
                      "songId": 999,
                      "rating": 3,
                      "reviewText": "Review for a phantom song."
                    }
                    """, TEST_USER1))
                .when()
                .post(BASE_PATH + "/review")
                .then()
                .statusCode(500);
    }

    // ============================================ CREDENTIAL CONTROLLER TESTS =======================================

    @Test
    public void createCredentialsSuccess() {
        RestAssured.given()
                .param("username", "newUser")
                .param("password", "secret")
                .param("accountType", "user")
                .when()
                .post("/credentials")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\":\"success\"}"));
    }

    @Test
    public void createCredentialsDuplicateUsername() {
        RestAssured.given()
                .param("username", "dupUser")
                .param("password", "secret")
                .param("accountType", "user")
                .when()
                .post("/credentials")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\":\"success\"}"));

        RestAssured.given()
                .param("username", "dupUser")
                .param("password", "another")
                .param("accountType", "user")
                .when()
                .post("/credentials")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\":\"An account already exists for that username.\"}"));
    }

    @Test
    public void verifyLoginValidCredentials() {
        createCredential("loginUser", "loginPass", "user");

        RestAssured.given()
                .param("username", "loginUser")
                .param("password", "loginPass")
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

    @Test
    public void verifyLoginInvalidPassword() {
        createCredential("loginUser2", "correctPass", "user");

        RestAssured.given()
                .param("username", "loginUser2")
                .param("password", "wrongPass")
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .body(equalTo("false"));
    }

    @Test
    public void verifyLoginUnknownUser() {
        RestAssured.given()
                .param("username", "unknownUser")
                .param("password", "whatever")
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .body(equalTo("false"));
    }

    @Test
    public void getAllCredentialsReturnsList() {
        createCredential("userA", "p1", "user");
        createCredential("userB", "p2", "admin");

        RestAssured.given()
                .when()
                .get("/credentials")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("username", hasItems("userA", "userB"))
                .body("accountType", hasItems("user", "admin"));
    }

    @Test
    public void getCredentialsByExistingUsername() {
        createCredential("singleUser", "p3", "user");

        RestAssured.given()
                .pathParam("username", "singleUser")
                .when()
                .get("/credentials/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo("singleUser"))
                .body("accountType", equalTo("user"));
    }

    @Test
    public void getCredentialsByNonExistingUsername() {
        RestAssured.given()
                .pathParam("username", "missingUser")
                .when()
                .get("/credentials/{username}")
                .then()
                .statusCode(200)
                .body(equalTo(""));
    }

    @Test
    public void updateCredentialsSuccess() {
        createCredential("updateUser", "oldPass", "user");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("username", "updateUser")
                .body("""
                {
                  "password": "newPass",
                  "accountType": "admin"
                }
                """)
                .when()
                .put("/credentials/{username}")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\":\"success\"}"));

        RestAssured.given()
                .param("username", "updateUser")
                .param("password", "newPass")
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

    @Test
    public void updateCredentialsMissingBody() {
        createCredential("updateUser2", "pass", "user");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("username", "updateUser2")
                .body("")
                .when()
                .put("/credentials/{username}")
                .then()
                .statusCode(400);
    }

    @Test
    public void updateCredentialsNonExistingUser() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("username", "noSuchUser")
                .body("""
                {
                  "username": "whatever",
                  "password": "p",
                  "accountType": "user"
                }
                """)
                .when()
                .put("/credentials/{username}")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\":\"failure\"}"));
    }

    @Test
    public void deleteCredentialsExistingUser() {
        createCredential("deleteUser", "pass", "user");

        RestAssured.given()
                .pathParam("username", "deleteUser")
                .when()
                .delete("/credentials/{username}")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\":\"success\"}"));

        // login should now fail (no credentials)
        RestAssured.given()
                .param("username", "deleteUser")
                .param("password", "pass")
                .when()
                .get("/login")
                .then()
                .statusCode(200)
                .body(equalTo("false"));
    }

    @Test
    public void deleteCredentialsNonExistingUser() {
        RestAssured.given()
                .pathParam("username", "ghostUser")
                .when()
                .delete("/credentials/{username}")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\":\"success\"}"));
    }

    // ============================================ SEARCH CONTROLLER TESTS =============================================

    public void searchProfiles1() {
        Profile p1 = new Profile();
        p1.setUsername("martin1");
        p1.setViews(5);
        profileRepository.save(p1);

        Profile p2 = new Profile();
        p2.setUsername("smart_user");
        p2.setViews(10);
        profileRepository.save(p2);

        Profile p3 = new Profile();
        p3.setUsername("martina");
        p3.setViews(1);
        profileRepository.save(p3);

        RestAssured.given()
                .pathParam("searchKey", "mart")
                .when()
                .get("/search/profiles/{searchKey}")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("[0]", equalTo("smart_user"))
                .body("[1]", equalTo("martin1"))
                .body("[2]", equalTo("martina"));
    }

    @Test
    public void searchProfiles2() {
        Profile p = new Profile();
        p.setUsername("otheruser");
        p.setViews(3);
        profileRepository.save(p);

        RestAssured.given()
                .pathParam("searchKey", "mart")
                .when()
                .get("/search/profiles/{searchKey}")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    public void incrementProfileViewsByUsername() {
        Profile p = new Profile();
        p.setUsername(JAM_MANAGER);
        p.setViews(2);
        profileRepository.save(p);

        RestAssured.given()
                .pathParam("username", JAM_MANAGER)
                .when()
                .put("/search/profiles/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(JAM_MANAGER))
                .body("views", equalTo(3));

        Profile updated = profileRepository.findById(JAM_MANAGER).orElseThrow();
        org.junit.Assert.assertEquals(3, updated.getViews());
    }

    @Test
    public void incrementProfileViewsNonExistingUser() {
        RestAssured.given()
                .pathParam("username", "missingUser")
                .when()
                .put("/search/profiles/{username}")
                .then()
                .statusCode(200)
                .body(equalTo(""));
    }

    @Test
    public void searchPlaylists1() {
        Playlist pl1 = new Playlist();
        pl1.setPlaylistName("ChillVibes");
        pl1.setUsername(TEST_USER1);
        pl1.setSearches(5);
        playlistRepository.save(pl1);

        Playlist pl2 = new Playlist();
        pl2.setPlaylistName("MartinsHits");
        pl2.setUsername(TEST_USER2);
        pl2.setSearches(10);
        playlistRepository.save(pl2);

        Playlist pl3 = new Playlist();
        pl3.setPlaylistName("RandomList");
        pl3.setUsername(REGULAR_USER);
        pl3.setSearches(3);
        playlistRepository.save(pl3);

        RestAssured.given()
                .pathParam("searchKey", "mart")
                .when()
                .get("/search/playlist/{searchKey}")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].playlistName", equalTo("MartinsHits"))
                .body("[0].username", equalTo(TEST_USER2))
                .body("[1].playlistName", equalTo("ChillVibes"))
                .body("[1].username", equalTo(TEST_USER1));
    }

    @Test
    public void searchPlaylistsNoMatches() {
        Playlist pl = new Playlist();
        pl.setPlaylistName("OtherList");
        pl.setUsername(REGULAR_USER);
        pl.setSearches(1);
        playlistRepository.save(pl);

        RestAssured.given()
                .pathParam("searchKey", "mart")
                .when()
                .get("/search/playlist/{searchKey}")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    public void incrementPlaylistSearchesByName() {
        Playlist pl = new Playlist();
        pl.setPlaylistName(TEST_JAM_NAME);
        pl.setUsername(JAM_MANAGER);
        pl.setSearches(0);
        playlistRepository.save(pl);

        RestAssured.given()
                .pathParam("playlistName", TEST_JAM_NAME)
                .when()
                .put("/search/playlist/{playlistName}")
                .then()
                .statusCode(200)
                .body("playlistName", equalTo(TEST_JAM_NAME))
                .body("searches", equalTo(1));

        Playlist updated = playlistRepository.findById(TEST_JAM_NAME).orElseThrow();
        org.junit.Assert.assertEquals(1, updated.getSearches());
    }

    @Test
    public void incrementPlaylistSearchesNonExisting() {
        RestAssured.given()
                .pathParam("playlistName", "UnknownList")
                .when()
                .put("/search/playlist/{playlistName}")
                .then()
                .statusCode(200)
                .body(equalTo(""));
    }

    @Test
    public void searchSongs1() {
        Song s1 = new Song("Martian Song", "ArtistA");
        s1.setSpotifyId("id3");
        s1.setSearches(5);
        songRepository.save(s1);

        Song s2 = new Song("Random", "Smart Artist");
        s2.setSpotifyId("id4");
        s2.setSearches(10);
        songRepository.save(s2);

        Song s3 = new Song("Other", "OtherArtist");
        s3.setSpotifyId("id5");
        s3.setSearches(1);
        songRepository.save(s3);

        RestAssured.given()
                .pathParam("searchKey", "mart")
                .when()
                .get("/search/songs/{searchKey}")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].songId", equalTo(s2.getSongId()))
                .body("[0].songName", equalTo(s2.getSongName()))
                .body("[0].artist", equalTo(s2.getArtist()))
                .body("[0].spotifyId", equalTo(s2.getSpotifyId()))
                .body("[1].songId", equalTo(s1.getSongId()))
                .body("[1].songName", equalTo(s1.getSongName()))
                .body("[1].artist", equalTo(s1.getArtist()))
                .body("[1].spotifyId", equalTo(s1.getSpotifyId()));
    }

    @Test
    public void searchSongsNoMatches() {
        Song s = new Song("Other", "OtherArtist");
        s.setSpotifyId("id3");
        s.setSearches(2);
        songRepository.save(s);

        RestAssured.given()
                .pathParam("searchKey", "mart")
                .when()
                .get("/search/songs/{searchKey}")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    public void incrementSongSearchesNonExisting() {
        RestAssured.given()
                .pathParam("songID", 9999)
                .when()
                .put("/search/songs/{songID}")
                .then()
                .statusCode(200)
                .body(equalTo(""));
    }

    // ============================================ VOTE TESTS =========================================================

    @Test
    public void voteDefaultStatusAndAccessors() {
        Jam jam = new Jam();
        jam.setName("JamForVoteDefault");
        jam = jamRepository.save(jam);

        Song song = new Song("VoteSongDefault", "VoteArtistDefault");
        song.setSpotifyId("vote-song-id-default");
        song = songRepository.save(song);

        Vote vote = new Vote(jam, song, "suggesterUser");
        vote = voteRepository.save(vote);

        org.junit.Assert.assertNotEquals(0, vote.getVoteId());
        org.junit.Assert.assertEquals("suggesterUser", vote.getSuggester());
        org.junit.Assert.assertEquals("inprogress", vote.getVoteStatus());
        org.junit.Assert.assertEquals("JamForVoteDefault", vote.getJam().getName());
        org.junit.Assert.assertEquals("VoteSongDefault", vote.getSong().getSongName());

        vote.setVoteStatus("closed");
        vote = voteRepository.save(vote);
        Vote reloaded = voteRepository.findById(vote.getVoteId()).orElseThrow();
        org.junit.Assert.assertEquals("closed", reloaded.getVoteStatus());
    }

    @Test
    public void voteNoArgConstructorAndSetters() {
        Jam jam = new Jam();
        jam.setName("JamNoArg");
        jam = jamRepository.save(jam);

        Song song = new Song("NoArgSong", "NoArgArtist");
        song.setSpotifyId("vote-song-id-noarg");
        song = songRepository.save(song);

        Vote vote = new Vote();
        vote.setJam(jam);
        vote.setSong(song);
        vote.setSuggester("setterUser");
        vote.setVoteStatus("pending");
        vote = voteRepository.save(vote);

        Vote found = voteRepository.findById(vote.getVoteId()).orElseThrow();
        org.junit.Assert.assertEquals("setterUser", found.getSuggester());
        org.junit.Assert.assertEquals("pending", found.getVoteStatus());
    }

    @Test
    public void userVoteAccessorsAndMultipleChoices() {
        Jam jam = new Jam();
        jam.setName("JamMultiVote");
        jam = jamRepository.save(jam);

        Song song = new Song("MultiSong", "MultiArtist");
        song.setSpotifyId("vote-song-id-multi");
        song = songRepository.save(song);

        Vote vote = new Vote(jam, song, "multiSuggester");
        vote = voteRepository.save(vote);

        UserVote yesVote = new UserVote(vote, "userYes", "yes");
        userVoteRepository.save(yesVote);

        UserVote noVote = new UserVote();
        noVote.setVote(vote);
        noVote.setUsername("userNo");
        noVote.setChoice("no");
        userVoteRepository.save(noVote);

        java.util.List<UserVote> all = userVoteRepository.findAll();
        org.junit.Assert.assertEquals(2, all.size());

        java.util.Map<String,String> byUser = new java.util.HashMap<>();
        all.forEach(v -> byUser.put(v.getUsername(), v.getChoice()));

        org.junit.Assert.assertEquals("yes", byUser.get("userYes"));
        org.junit.Assert.assertEquals("no", byUser.get("userNo"));
    }


    @Test
    public void saveJamMessagePersistsFieldsAndRelation() {
        Jam jam = new Jam();
        jam.setName("ChatJam");
        jam = jamRepository.save(jam);

        cycloneSounds.Jams.JamMessage msg = new cycloneSounds.Jams.JamMessage("user1", jam, "Hello world");
        msg = jamMessageRepository.save(msg);

        cycloneSounds.Jams.JamMessage found = jamMessageRepository.findById(msg.getId()).orElseThrow();

        org.junit.Assert.assertNotNull(found.getId());
        org.junit.Assert.assertEquals("user1", found.getUserName());
        org.junit.Assert.assertEquals("Hello world", found.getContent());
        org.junit.Assert.assertEquals("ChatJam", found.getJam().getName());
        org.junit.Assert.assertEquals("CHAT", found.getMessageType());
        org.junit.Assert.assertNotNull(found.getSent());
    }

    @Test
    public void defaultConstructorSetsSentAndMessageType() {
        Jam jam = new Jam();
        jam.setName("ChatJam2");
        jam = jamRepository.save(jam);

        cycloneSounds.Jams.JamMessage msg = new cycloneSounds.Jams.JamMessage();
        msg.setUserName("user2");
        msg.setJam(jam);
        msg.setContent("Hi there");
        msg = jamMessageRepository.save(msg);

        cycloneSounds.Jams.JamMessage found = jamMessageRepository.findById(msg.getId()).orElseThrow();

        org.junit.Assert.assertEquals("CHAT", found.getMessageType());
        org.junit.Assert.assertNotNull(found.getSent());
    }
}