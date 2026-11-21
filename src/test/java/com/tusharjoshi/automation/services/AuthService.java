package com.tusharjoshi.automation.services;

import com.tusharjoshi.automation.base.Specs;

import static io.restassured.RestAssured.given;

public class AuthService {

    public String getToken(String username, String password) {
        return given()
                .spec(Specs.request())
                .body("{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }")
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract().path("token");
    }
}