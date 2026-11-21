package com.tusharjoshi.automation.tests;   // change to match your package

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BookingApiTest {

    private String token;
    private int bookingId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    // 1. Get an auth token (needed for PUT, PATCH, DELETE)
    @Test(priority = 1)
    public void createToken() {
        String body = """
            {
              "username": "admin",
              "password": "password123"
            }
            """;

        token = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().path("token");

        System.out.println("Token: " + token);
    }

    // 2. Create a booking (POST)
    @Test(priority = 2)
    public void createBooking() {
        String body = """
            {
              "firstname": "Tushar",
              "lastname": "Joshi",
              "totalprice": 500,
              "depositpaid": true,
              "bookingdates": { "checkin": "2026-12-01", "checkout": "2026-12-05" },
              "additionalneeds": "Breakfast"
            }
            """;

        bookingId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .body("booking.firstname", equalTo("Tushar"))
                .body("booking.totalprice", equalTo(500))
                .extract().path("bookingid");

        System.out.println("Created booking id: " + bookingId);
    }

    // 3. Read the booking (GET by id)
    @Test(priority = 3, dependsOnMethods = "createBooking")
    public void getBookingById() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Tushar"))
                .body("lastname", equalTo("Joshi"))
                .body("bookingdates.checkin", equalTo("2026-12-01"));
    }

    // 4. Full update (PUT) - needs token
    @Test(priority = 4, dependsOnMethods = {"createToken", "createBooking"})
    public void updateBooking() {
        String body = """
            {
              "firstname": "Tushar",
              "lastname": "Joshi",
              "totalprice": 750,
              "depositpaid": false,
              "bookingdates": { "checkin": "2026-12-10", "checkout": "2026-12-15" },
              "additionalneeds": "Dinner"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(body)
                .when()
                .put("/booking/{id}")
                .then()
                .statusCode(200)
                .body("totalprice", equalTo(750))
                .body("additionalneeds", equalTo("Dinner"));
    }

    // 5. Partial update (PATCH) - needs token
    @Test(priority = 5, dependsOnMethods = {"createToken", "createBooking"})
    public void partialUpdateBooking() {
        String body = """
            {
              "firstname": "Tush"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(body)
                .when()
                .patch("/booking/{id}")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Tush"))
                .body("lastname", equalTo("Joshi"));   // unchanged
    }

    // 6. Delete (DELETE) - needs token
    @Test(priority = 6, dependsOnMethods = {"createToken", "createBooking"})
    public void deleteBooking() {
        given()
                .cookie("token", token)
                .pathParam("id", bookingId)
                .when()
                .delete("/booking/{id}")
                .then()
                .statusCode(201);   // this practice API returns 201 for a successful delete
    }

    // 7. Confirm it's gone (negative check)
    @Test(priority = 7, dependsOnMethods = "deleteBooking")
    public void getDeletedBookingReturns404() {
        given()
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(404);
    }
}