package com.example.api.tests;

import com.example.api.pojos.Todo;
import com.example.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests against https://jsonplaceholder.typicode.com — free fake REST API.
 */
public class JsonPlaceholderTests {

    private static final Logger log = LoggerFactory.getLogger(JsonPlaceholderTests.class);
    private static final String BASE_URL = TestConfig.JSONPLACEHOLDER_BASE_URL;

    private static RequestSpecification reqSpec;
    private static ResponseSpecification resSpec;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = BASE_URL;

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        resSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();

        log.info("JsonPlaceholder ready — base: {}", BASE_URL);
    }

    @Test(description = "GET all todos")
    public void shouldGetAllTodos() {
        List<Todo> todos = given()
                .spec(reqSpec)
        .when()
                .get("/todos")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract()
                .jsonPath().getList(".", Todo.class);

        assertThat(todos).isNotEmpty();
        assertThat(todos).allMatch(t -> t.getId() != null);
        log.info("Fetched {} todos", todos.size());
    }

    @Test(description = "GET single todo by id")
    public void shouldGetSingleTodo() {
        Todo todo = given()
                .spec(reqSpec)
                .pathParam("id", 1)
        .when()
                .get("/todos/{id}")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract().as(Todo.class);

        assertThat(todo.getId()).isEqualTo(1);
        assertThat(todo.getUserId()).isEqualTo(1);
        assertThat(todo.getTitle()).isNotBlank();
        assertThat(todo.getCompleted()).isFalse();
    }

    @Test(description = "POST create a new todo")
    public void shouldCreateTodo() {
        Todo newTodo = Todo.builder()
                .userId(1)
                .title("Learn REST Assured")
                .completed(false)
                .build();

        Todo created = given()
                .spec(reqSpec)
                .body(newTodo)
        .when()
                .post("/todos")
        .then()
                .spec(resSpec)
                .statusCode(201)
                .extract().as(Todo.class);

        assertThat(created.getId()).isEqualTo(201);  // jsonplaceholder always returns 201
        assertThat(created.getTitle()).isEqualTo("Learn REST Assured");

        log.info("Created todo id={}", created.getId());
    }

    @Test(description = "PATCH update todo title")
    public void shouldPatchTodo() {
        String body = """
                { "title": "Patched title" }
                """;

        Todo patched = given()
                .spec(reqSpec)
                .pathParam("id", 1)
                .body(body)
        .when()
                .patch("/todos/{id}")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract().as(Todo.class);

        assertThat(patched.getTitle()).isEqualTo("Patched title");
    }

    @Test(description = "DELETE a todo")
    public void shouldDeleteTodo() {
        given()
                .spec(reqSpec)
                .pathParam("id", 1)
        .when()
                .delete("/todos/{id}")
        .then()
                .statusCode(200);
    }

    @Test(description = "GET /comments?postId=1 — query parameter filtering")
    public void shouldFilterCommentsByPostId() {
        Response response = given()
                .spec(reqSpec)
                .queryParam("postId", 1)
        .when()
                .get("/comments")
        .then()
                .spec(resSpec)
                .statusCode(200)
                .extract().response();

        List<String> postIds = response.jsonPath().getList("postId").stream()
                .map(Object::toString)
                .toList();

        assertThat(postIds).isNotEmpty();
        assertThat(postIds).allMatch(id -> "1".equals(id));
        log.info("Comments filtered by postId=1: {} results", postIds.size());
    }
}
