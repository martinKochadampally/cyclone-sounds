package cycloneSounds;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class MartinSystemTest {
    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void credentialsPostTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                param("username", "user2").
                param("password", "password2").
                param("accountType", "regular").
                when().
                post("/credentials");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

//        String returnString = response.getBody().asString();
//        assertEquals("{\"message\":\"success\"}", returnString);
    }



    /**
     * Checks you can sucessfully create a Jam.
     */
    @Test
    public void jamPostTest1() {
        RestAssured.given()
                .param("username", "user1")
                .param("password", "password1")
                .param("accountType", "jamManager")
                .when()
                .post("/credentials");

        RestAssured.given()
                .when()
                .get("/credentials/" + "user1")
                .then()
                .statusCode(200);

        Response response = RestAssured.given()
                .pathParam("username", "user1")
                .pathParam("jamName", "jam-test1")
                .pathParam("approvalType", "Voting")
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}");

        assertEquals(200, response.getStatusCode());

        String body = response.getBody().asString();
        JSONObject json = new JSONObject(body);
        assertEquals("jam-test1", json.getString("name"));
        assertEquals("user1", json.getString("manager"));
    }

    /**
     * Checks that a regular user is unable to make a jam.
     */
    @Test
    public void jamPostTest2() {
        RestAssured.given()
                .param("username", "user3")
                .param("password", "password3")
                .param("accountType", "regular").
                when()
                .post("/credentials");

        RestAssured.given()
                .pathParam("username", "user3")
                .pathParam("jamName", "jam3")
                .pathParam("approvalType", "Voting")
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then().statusCode(403);
    }

    /**
     * Checks that two different jam managers cannot make the jams with the same name
     */
    @Test
    public void jamPostTest3() {
        RestAssured.given()
                .param("username", "user4")
                .param("password", "password4")
                .param("accountType", "jamManager")
                .when()
                .post("/credentials");

        RestAssured.given()
                .param("username", "user5").
                param("password", "password5")
                .param("accountType", "jamManager")
                .when()
                .post("/credentials");

        RestAssured.given()
                .pathParam("username", "user4")
                .pathParam("jamName", "jam")
                .pathParam("approvalType", "Voting")
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}");

        RestAssured.given()
                .pathParam("username", "user5")
                .pathParam("jamName", "jam")
                .pathParam("approvalType", "Voting")
                .when()
                .post("/api/jams/{username}/{jamName}/{approvalType}")
                .then().
                statusCode(400);
    }

}
