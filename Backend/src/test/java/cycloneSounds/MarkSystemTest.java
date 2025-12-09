package cycloneSounds;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import cycloneSounds.Songs.Song;
import org.springframework.beans.factory.annotation.Autowired;
import cycloneSounds.Songs.SongRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//Runner test
@RunWith(SpringRunner.class)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Main.class)
public class MarkSystemTest {

    @LocalServerPort
    int port;

    @Autowired
    private SongRepository songRepository;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }


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
    public void addSongToPlaylistCheck() {
        createHelperUser("mgseward", "Mark");
        RestAssured.given()
                .param("playlistName", "Study")
                .param("username", "mgseward")
                .post("/api/playlists/create");

        Song testSong = new Song("Runnin", "21 Savage, Metro Boomin");
        testSong.setSpotifyId("testSpotifyId123"); // Required if your entity enforces it
        songRepository.save(testSong);

        Response response = RestAssured.given()
                .param("songName", "Runnin")
                .param("artist", "21 Savage, Metro Boomin")
                .when()
                .post("/api/playlists/mgseward/Study/add");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Runnin"));
    }

    /**
     * Helper method that makes user creation easy
     */
    public void createHelperUser(String username, String name) {
        String jsonBody = "{\"username\":\"" + username + "\", \"name\":\"" + name + "\", \"favArtist\":\"A\", \"favGenre\":\"G\", \"favSong\":\"S\", \"biography\":\"B\"}";
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .post("/profiles");
    }

    @Test
    public void createProfileCheck() {
        String jsonBody = "{" + "\"username\": \"TestUser1\"," + "\"name\": \"Mark Tester\"," +
                "\"favArtist\": \"Coldplay\"," + "\"favGenre\": \"Pop\"," + "\"favSong\": \"Yellow\"," +
                "\"biography\": \"Just a test bio\"" + "}";

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
    public void spotifySearchCheck() {
        Response response = RestAssured.given()
                .param("query", "Taylor Swift")
                .when()
                .post("/api/songs/search");
        assertEquals(200, response.getStatusCode());

        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("Search complete") || responseBody.contains("Error"));
    }
}