package cycloneSounds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static org.junit.Assert.*;

public class MarkSystemTest {


    @Test
    public void postReviewCheck() {
        RestAssured.baseURI = "http://localhost:8080";

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
        RestAssured.baseURI = "http://localhost:8080";

        createHelperUser("mgseward", "Mark");
        RestAssured.given()
                .param("playlistName", "Study")
                .param("username", "mgseward")
                .post("/api/playlists/create");

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
     * @param username
     * @param name
     */
    public void createHelperUser(String username, String name) {
        String jsonBody = "{\"username\":\"" + username + "\", \"name\":\"" + name + "\", \"favArtist\":\"A\", \"favGenre\":\"G\", \"favSong\":\"S\", \"biography\":\"B\"}";
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .post("http://localhost:8080/profiles");
    }


    @Test
    public void createProfileCheck() {
        RestAssured.baseURI = "http://localhost:8080";

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
        RestAssured.baseURI = "http://localhost:8080";

        Response response = RestAssured.given()
                .param("query", "Taylor Swift")
                .when()
                .post("/api/songs/search");


        assertEquals(200, response.getStatusCode());

        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("Search complete") || responseBody.contains("Error"));
    }
}