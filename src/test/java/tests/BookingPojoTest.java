package tests;

import com.tusharjoshi.automation.pojo.Booking;
import com.tusharjoshi.automation.pojo.BookingDates;
import com.tusharjoshi.automation.pojo.BookingResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BookingPojoTest {


    private static int bookingId;
    private static Booking requestBooking;

    @BeforeTest
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

    }

    @Test(priority = 1)
    public void createBookingwithPOJO() {
        requestBooking = new Booking("Tushar", "Joshi", 5000, true,
                new BookingDates("2026-12-20", "2026-12-28"),
                "Breakfast");

        LocalDate chkin = LocalDate.parse(requestBooking.getBookingdates().getCheckin());
        LocalDate chkout = LocalDate.parse(requestBooking.getBookingdates().getCheckout());

        System.out.println(chkin + " " + chkout);
        Assert.assertTrue(chkout.isAfter(chkin));
        BookingResponse response =
                given()
                        .contentType(ContentType.JSON)
                        .body(requestBooking)
                        .when()
                        .post("/booking")
                        .then()
                        .statusCode(200)
                        .extract().as(BookingResponse.class);
        bookingId = response.getBookingid();
        Booking created = response.getBooking();
        System.out.println("Booking is completed ");
        System.out.println("Created booking id: " + bookingId);

        Assert.assertTrue(bookingId > 0);
        Assert.assertEquals(created.getFirstname(), "Tushar");
        Assert.assertEquals(created.getTotalprice(), 5000);
        Assert.assertTrue(created.isDepositpaid());
        Assert.assertEquals(created.getBookingdates().getCheckin(), "2026-12-20");
    }


    @Test(dependsOnMethods = "createBookingwithPOJO")
    public void getBookingasPOJO() {
        Booking fetched = given()
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .extract().as(Booking.class);

        Assert.assertEquals(fetched.getFirstname(), requestBooking.getFirstname());
        Assert.assertEquals(fetched.getLastname(), requestBooking.getLastname());
        Assert.assertEquals(fetched.getTotalprice(), requestBooking.getTotalprice());
        Assert.assertEquals(fetched.isDepositpaid(), requestBooking.isDepositpaid());
        Assert.assertEquals(fetched.getBookingdates().getCheckout(),
                requestBooking.getBookingdates().getCheckout());
        Assert.assertEquals(fetched.getAdditionalneeds(), requestBooking.getAdditionalneeds());

        System.out.println("Deposit: " + (fetched.isDepositpaid() ? "Paid" : "Unpaid"));
    }

    @Test(dependsOnMethods = "getBookingasPOJO")
    public void updateBooking() {
        String token =
                given()
                        .contentType(ContentType.JSON)
                        .body("{ \"username\": \"admin\", \"password\": \"password123\" }")
                        .when()
                        .post("/auth")
                        .then()
                        .statusCode(200)
                        .extract().path("token");

        System.out.println(token);
        requestBooking.setTotalprice(9999);
        requestBooking.setAdditionalneeds("Airport Pickdrop");
        Booking updated =
                given()
                        .contentType(ContentType.JSON)
                        .cookie("token", token)
                        .pathParam("id", bookingId)
                        .body(requestBooking)
                        .when()
                        .put("/booking/{id}")
                        .then().statusCode(200)
                        .extract().as(Booking.class);
        Assert.assertEquals(updated.getTotalprice(), 9999);
        Assert.assertEquals(updated.getAdditionalneeds(), "Airport Pickdrop");
        System.out.println("Updated price: " + updated.getTotalprice());

    }

    @Test(dependsOnMethods = "updateBooking")
    public void getBookingMatchesSchema() {

        given()
                .accept("application/json")
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .log().all();
        given()
                .accept("application/json")
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }



    /*@DataProvider(name = "allBookingIds")
    public Object[][] allBookingIds() {
        List<Integer> ids = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .accept("application/json")
                .get("/booking")
                .then().extract().jsonPath().getList("bookingid", Integer.class);

        // keep it reasonable while learning: first 20 only
        List<Integer> sample = ids.subList(0, Math.min(20, ids.size()));

        Object[][] data = new Object[sample.size()][1];
        for (int i = 0; i < sample.size(); i++) {
            data[i][0] = sample.get(i);
        }
        return data;
    }

    @Test(dataProvider = "allBookingIds",dependsOnMethods = "getBookingMatchesSchema")
    public void bookingMatchesSchema(int bookingId) {
        given()
                .accept("application/json")
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }*/
}

