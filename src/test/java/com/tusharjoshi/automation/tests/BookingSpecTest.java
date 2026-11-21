package com.tusharjoshi.automation.tests;

import com.tusharjoshi.automation.base.ApiBaseTest;
import com.tusharjoshi.automation.pojo.Booking;
import com.tusharjoshi.automation.pojo.BookingDates;
import com.tusharjoshi.automation.pojo.BookingResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class BookingSpecTest extends ApiBaseTest {

    private static int bookingId;

    @Test(priority = 1)
    public void createBooking() {
        Booking booking = new Booking("Tushar", "Joshi", 5000, true,
                new BookingDates("2026-12-20", "2026-12-25"), "Breakfast");

        BookingResponse response =
                given()
                        .spec(requestSpec)
                        .body(booking)
                        .when()
                        .post("/booking")
                        .then()
                        .spec(okResponseSpec)
                        .extract().as(BookingResponse.class);

        bookingId = response.getBookingid();
        Assert.assertEquals(response.getBooking().getFirstname(), "Tushar");
    }

    @Test(priority = 2, dependsOnMethods = "createBooking")
    public void getBooking() {
        given()
                .spec(requestSpec)
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .spec(okResponseSpec)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"))
                .body("firstname", equalTo("Tushar"));
    }

    @Test(priority = 3, dependsOnMethods = "getBooking")
    public void patchBooking() {
        given()
                .spec(authRequestSpec)                     // token included automatically
                .pathParam("id", bookingId)
                .body("{ \"firstname\": \"Tush\" }")
                .when()
                .patch("/booking/{id}")
                .then()
                .spec(okResponseSpec)
                .body("firstname", equalTo("Tush"));
    }

    @Test(priority = 4, dependsOnMethods = "patchBooking")
    public void deleteBooking() {
        given()
                .spec(authRequestSpec)
                .pathParam("id", bookingId)
                .when()
                .delete("/booking/{id}")
                .then()
                .statusCode(201);                          // no okResponseSpec: DELETE isn't 200 + JSON here
    }

    @Test(priority = 5, dependsOnMethods = "deleteBooking")
    public void getAfterDeleteReturns404() {
        given()
                .spec(requestSpec)
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(404);
    }
}