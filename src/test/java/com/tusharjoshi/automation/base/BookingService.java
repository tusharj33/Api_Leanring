package com.tusharjoshi.automation.services;

import com.tusharjoshi.automation.base.Specs;
import com.tusharjoshi.automation.pojo.Booking;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingService {

    private static final String BOOKING = "/booking";
    private static final String BOOKING_BY_ID = "/booking/{id}";
    @Step("Create booking for {booking.firstname} {booking.lastname}")
    public Response createBooking(Booking booking) {
        return given().spec(Specs.request())
                .body(booking)
                .post(BOOKING);
    }
    @Step("Get booking with id {id}")
    public Response getBooking(int id) {
        return given().spec(Specs.request())
                .pathParam("id", id)
                .get(BOOKING_BY_ID);
    }

    public Response getBookingIds() {
        return given().spec(Specs.request())
                .get(BOOKING);
    }

    public Response updateBooking(int id, Booking booking, String token) {
        return given().spec(Specs.authRequest(token))
                .pathParam("id", id)
                .body(booking)
                .put(BOOKING_BY_ID);
    }

    public Response patchBooking(int id, Map<String, Object> fields, String token) {
        return given().spec(Specs.authRequest(token))
                .pathParam("id", id)
                .body(fields)
                .patch(BOOKING_BY_ID);
    }
    @Step("Delete booking {id}")
    public Response deleteBooking(int id, String token) {
        return given().spec(Specs.authRequest(token))
                .pathParam("id", id)
                .delete(BOOKING_BY_ID);
    }
}