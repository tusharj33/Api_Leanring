package com.tusharjoshi.automation.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

public class ApiBaseTest {

    protected static RequestSpecification requestSpec;       // no auth
    protected static RequestSpecification authRequestSpec;   // with token
    protected static ResponseSpecification okResponseSpec;   // 200 + JSON + fast
    protected static String token;

    @BeforeClass
    public void baseSetup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://restful-booker.herokuapp.com")
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .log(LogDetail.URI)                     // print just the URL of each request
                .build();

        token = given()
                .spec(requestSpec)
                .body("{ \"username\": \"admin\", \"password\": \"password123\" }")
                .post("/auth")
                .then()
                .statusCode(200)
                .extract().path("token");

        authRequestSpec = new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)  // inherit everything from requestSpec
                .addCookie("token", token)              // plus the token
                .build();

        okResponseSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(5000L))
                .build();
    }
}