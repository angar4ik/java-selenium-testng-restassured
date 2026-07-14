package com.example.api.base;

import com.example.config.EnvLoader;
import com.example.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

import static com.example.config.TestConfig.REQRES_BASE_URL;

/**
 * Base class for API tests against reqres.in.
 * Sets up base URI, request/response specs, and enables logging.
 */
public abstract class BaseApiTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);

    protected static RequestSpecification reqSpec;
    protected static ResponseSpecification resSpec;

    // Load .env (if present) before any config constants are resolved.
    // This ensures that TestConfig.REQRES_API_KEY can read system properties
    // set by EnvLoader for local development.
    static {
        EnvLoader.loadIfPresent();
    }

    @BeforeClass
    public static void setUpApi() {
        RestAssured.baseURI = REQRES_BASE_URL;

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("x-api-key", TestConfig.REQRES_API_KEY)
                .log(LogDetail.ALL)
                .build();

        resSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        log.info("API config ready — base URI: {}", REQRES_BASE_URL);
    }
}
