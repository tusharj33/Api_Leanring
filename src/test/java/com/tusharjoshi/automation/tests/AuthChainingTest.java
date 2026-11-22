package com.tusharjoshi.automation.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Listeners(com.tusharjoshi.automation.tests.TestListener.class)
public class AuthChainingTest {

    public static String accessToken;
    public static int userId;
    public static String uri, username, password;

    @BeforeClass
    public void setUp() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("src/test/java/com/tusharjoshi/automation/tests/info.properties"));
        uri = props.getProperty("base.uri");
        username = props.getProperty("api.username");
        password = props.getProperty("api.password");
        RestAssured.baseURI = uri;
        login();
    }

    static void login() {


        String body = """
                {"username":"%s","password":"%s"}
                """.formatted(username, password);

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
    public void getCurrentUser_headerWay() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("username", equalTo("emilys"));

    }

    @Test()
    public void getCurrentUser_oauth2Way() {
        given().contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("username", equalTo("emilys"));
    }

    @Test
    public void noToken_shouldFail() {
        given()
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401);
    }
}
