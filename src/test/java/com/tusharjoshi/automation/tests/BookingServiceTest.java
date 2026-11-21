package com.tusharjoshi.automation.tests;

import com.tusharjoshi.automation.base.Specs;
import com.tusharjoshi.automation.pojo.Booking;
import com.tusharjoshi.automation.pojo.BookingResponse;
import com.tusharjoshi.automation.services.AuthService;
import com.tusharjoshi.automation.services.BookingService;
import com.tusharjoshi.automation.testdata.BookingData;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.qameta.allure.*;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
@Epic("Hotel Booking API")
@Feature("Booking management")
public class BookingServiceTest {

    private final BookingService bookingService = new BookingService();
    private final AuthService authService = new AuthService();
    private String token;

    @BeforeClass
    public void setup() {
        token = authService.getToken("admin", "password123");
    }

    @Test
    public void createAndFetchBooking() {
        Booking request = BookingData.validBooking();

        BookingResponse created = bookingService.createBooking(request)
                .then().spec(Specs.ok())
                .extract().as(BookingResponse.class);

        int id = created.getBookingid();

        bookingService.getBooking(id)
                .then().spec(Specs.ok())
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));

        Booking fetched = bookingService.getBooking(id).as(Booking.class);
        Assert.assertEquals(fetched.getFirstname(), request.getFirstname());
        Assert.assertEquals(fetched.getTotalprice(), request.getTotalprice());

        bookingService.deleteBooking(id, token);       // clean up our own data
    }

    @Test
    public void patchFirstname() {
        int id = bookingService.createBooking(BookingData.validBooking())
                .then().spec(Specs.ok())
                .extract().path("bookingid");

        Response patched = bookingService.patchBooking(id, Map.of("firstname", "Tush"), token);

        patched.then().spec(Specs.ok());
        Assert.assertEquals(patched.jsonPath().getString("firstname"), "Tush");
        Assert.assertEquals(patched.jsonPath().getInt("totalprice"), 5000);   // unchanged

        bookingService.deleteBooking(id, token);
    }

    @Test
    public void deletedBookingReturns404() {
        int id = bookingService.createBooking(BookingData.validBooking())
                .then().extract().path("bookingid");

        Response deleted = bookingService.deleteBooking(id, token);
        Assert.assertEquals(deleted.getStatusCode(), 201);   // restful-booker quirk

        Assert.assertEquals(bookingService.getBooking(id).getStatusCode(), 404);
    }

    @Test
    public void updateWithoutTokenIsRejected() {
        int id = bookingService.createBooking(BookingData.validBooking())
                .then().extract().path("bookingid");

        Response res = bookingService.updateBooking(id, BookingData.validBooking(), "invalid-token");
        Assert.assertEquals(res.getStatusCode(), 403);

        bookingService.deleteBooking(id, token);
    }
}