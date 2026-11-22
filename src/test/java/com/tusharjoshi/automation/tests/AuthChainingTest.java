package com.tusharjoshi.automation.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Listeners(com.tusharjoshi.automation.tests.TestListener.class)
public class AuthChainingTest {

    public static String accessToken;
    public static int userId;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://dummyjson.com";
        login();
    }

    static void login() {
        String body = """
                {
                  "username": "emilys",
                  "password": "emilyspass",
                  "expiresInMins": 30
                }
                """;
        Response res = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        accessToken = res.jsonPath().getString("accessToken");
        userId = res.jsonPath().getInt("id");

        System.out.println("Token: " + accessToken.substring(0, 20) + "...");
        System.out.println("User id: " + userId);

    }

    @Test()
    public  void getCurrentUser_headerWay(){
        given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer "+accessToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("username",equalTo("emilys"));

    }

    @Test()
    public void getCurrentUser_oauth2Way() {
        given().contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("username",equalTo("emilys"));
    }
@Test
    public void noToken_shouldFail() {
        given()
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401);
    enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
