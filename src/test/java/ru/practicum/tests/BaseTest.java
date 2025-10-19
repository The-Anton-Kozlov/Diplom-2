package ru.practicum.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.Before;
import ru.practicum.config.RestConfig;
import static io.restassured.config.LogConfig.logConfig;

public class BaseTest {

    @Before
    public void startUp() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(RestConfig.HOST)
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.config = RestAssured.config()
                .logConfig(logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails());
    }

}

