package com.example.api.tests;

import com.example.api.base.BaseApiTest;
import com.example.api.pojos.User;
import com.example.api.pojos.UsersResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests against https://reqres.in — a free, no-auth REST API for learning.
 */
public class ReqResTests extends BaseApiTest {

    // ── GET /users?page=2 ──────────────────────────────────────────────

    @Test(description = "GET list of users on page 2")
    public void shouldGetListOfUsers() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("page", 2)
        .when()
                .get("/users")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract().response();

        UsersResponse usersResponse = response.as(UsersResponse.class);

        assertThat(usersResponse.getPage()).isEqualTo(2);
        assertThat(usersResponse.getPerPage()).isEqualTo(6);
        assertThat(usersResponse.getTotal()).isPositive();
        assertThat(usersResponse.getTotalPages()).isPositive();
        assertThat(usersResponse.getData()).hasSize(6);
        assertThat(usersResponse.getData().get(0).getEmail()).contains("@reqres.in");
    }

    // ── GET /users/{id} ────────────────────────────────────────────────

    @Test(description = "GET single user by id")
    public void shouldGetSingleUser() {
        Response response = given()
                .spec(reqSpec)
                .pathParam("id", 2)
        .when()
                .get("/users/{id}")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract().response();

        // reqres wraps single-user responses in { data: {...} }
        User user = response.jsonPath().getObject("data", User.class);

        assertThat(user.getId()).isEqualTo(2);
        assertThat(user.getEmail()).isEqualTo("janet.weaver@reqres.in");
        assertThat(user.getFirstName()).isEqualTo("Janet");
        assertThat(user.getLastName()).isEqualTo("Weaver");
    }

    // ── GET /users/{id} — 404 ──────────────────────────────────────────

    @Test(description = "GET non-existing user returns 404")
    public void shouldReturn404ForNonExistingUser() {
        given()
                .spec(reqSpec)
                .pathParam("id", 9999)
        .when()
                .get("/users/{id}")
        .then()
                .spec(resSpec)
                .statusCode(404);
    }

    // ── POST /users — create ───────────────────────────────────────────

    @Test(description = "POST create a new user")
    public void shouldCreateUser() {
        String requestBody = """
                {
                    "name": "John Doe",
                    "job": "Tester"
                }
                """;

        Response response = given()
                .spec(reqSpec)
                .body(requestBody)
        .when()
                .post("/users")
        .then()
                .spec(resSpec)
                .statusCode(201)
                .extract().response();

        assertThat(response.jsonPath().getString("name")).isEqualTo("John Doe");
        assertThat(response.jsonPath().getString("job")).isEqualTo("Tester");
        assertThat(response.jsonPath().getString("id")).isNotEmpty();
        assertThat(response.jsonPath().getString("createdAt")).isNotEmpty();

        log.info("Created user id={}, createdAt={}",
                response.jsonPath().getString("id"),
                response.jsonPath().getString("createdAt"));
    }

    // ── PUT /users/{id} — update ───────────────────────────────────────

    @Test(description = "PUT update a user")
    public void shouldUpdateUser() {
        String requestBody = """
                {
                    "name": "Jane Doe",
                    "job": "Senior Tester"
                }
                """;

        Response response = given()
                .spec(reqSpec)
                .pathParam("id", 2)
                .body(requestBody)
        .when()
                .put("/users/{id}")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract().response();

        assertThat(response.jsonPath().getString("name")).isEqualTo("Jane Doe");
        assertThat(response.jsonPath().getString("job")).isEqualTo("Senior Tester");
        assertThat(response.jsonPath().getString("updatedAt")).isNotEmpty();
    }

    // ── POST /login — successful ───────────────────────────────────────

    @Test(description = "POST login — success")
    public void shouldLoginSuccessfully() {
        String body = """
                {
                    "email": "eve.holt@reqres.in",
                    "password": "cityslicka"
                }
                """;

        Response response = given()
                .spec(reqSpec)
                .body(body)
        .when()
                .post("/login")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract().response();

        assertThat(response.jsonPath().getString("token")).isNotEmpty();
        log.info("Token: {}", response.jsonPath().getString("token"));
    }

    // ── POST /login — failure ──────────────────────────────────────────

    @Test(description = "POST login — missing password returns 400")
    public void shouldFailLoginWithoutPassword() {
        String body = """
                { "email": "eve.holt@reqres.in" }
                """;

        Response response = given()
                .spec(reqSpec)
                .body(body)
        .when()
                .post("/login")
        .then()
                .spec(resSpec)
                .statusCode(400)
                .extract().response();

        assertThat(response.jsonPath().getString("error"))
                .isEqualTo("Missing password");
    }

    // ── GET /users?delay=3 — delayed response ──────────────────────────

    @Test(description = "GET users with delay (timeout validation)")
    public void shouldHandleDelayedResponse() {
        long start = System.currentTimeMillis();

        given()
                .spec(reqSpec)
                .queryParam("delay", 2)   // 2-second delay
        .when()
                .get("/users")
        .then()
                .spec(resSpec)
                .statusCode(200);

        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed).isGreaterThanOrEqualTo(2000);
        log.info("Delayed response received in {} ms", elapsed);
    }
}
