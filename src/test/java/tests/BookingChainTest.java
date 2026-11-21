package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BookingChainTest {
    private static String token;
    private static int bookinId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    //is API alive
    @Test(priority = 0)
    public void healthCheck() {
        given()
                .when()
                .get("/ping")
                .then()
                .statusCode(201);
    }

    @Test(priority = 1, dependsOnMethods = "healthCheck")
    public void getToken() {
        token = given()
                .contentType(ContentType.JSON)
                .body("""
                        { "username": "admin", "password": "password123" }
                        """)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().path("token");
        System.out.println("Token: " + token);

    }

    @Test(dependsOnMethods = "getToken")
    public void createBooking() {
        String reqbody = """
                {
                  "firstname": "Tushar",
                  "lastname": "Joshi",
                  "totalprice": 5000,
                  "depositpaid": true,
                  "bookingdates": {
                    "checkin": "2026-12-20",
                    "checkout": "2026-12-25"
                  },
                  "additionalneeds": "Breakfast"
                }
                """;
        bookinId =
                given()
                        .contentType(ContentType.JSON)
                        //.accept(ContentType.JSON)
                        .body(reqbody)
                        .when()
                        .post("/booking")
                        .then()
                        .statusCode(200)
                        .body("bookingid", notNullValue())
                        .body("booking.firstname", equalTo("Tushar"))
                        .body("booking.bookingdates.checkin", equalTo("2026-12-20"))
                        .extract().path("bookingid");
        System.out.println("booking id is " + bookinId);
    }

    @Test(dependsOnMethods = "createBooking")
    public void getBooking() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", bookinId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Tushar"))
                .body("lastname", equalTo("Joshi"))
                .body("bookingdates.checkin", equalTo("2026-12-20"));

    }

    @Test(dependsOnMethods = "getBooking")
    public void putRequest() {
        String body = """
                {
                  "firstname": "Tushar",
                  "lastname": "Joshi",
                  "totalprice": 7500,
                  "depositpaid": false,
                  "bookingdates": {
                    "checkin": "2026-12-21",
                    "checkout": "2026-12-28"
                  },
                  "additionalneeds": "Dinner"
                }
                """;
        given()
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + token)
                .pathParam("id", bookinId)
                .body(body)
                .when()
                .put("/booking/{id}")
                .then()
                .statusCode(200)
                .body("additionalneeds", equalTo("Dinner"));


    }

    @AfterClass
    public void showBookings() {
        if (bookinId < 1)
            return;
        Response response = given()
                .pathParam("id", bookinId)
                .when()
                .get("/booking/{id}");
        JsonPath js = response.jsonPath();
        System.out.println("------------------------------------------------------------------");
        System.out.println("Booking ID " + bookinId);
        System.out.println("Name " + js.getString("firstname") + " " + js.getString("lastname"));
        System.out.println("Checkin date " + js.getString("bookingdates.checkin"));
        System.out.println("Checkout date " + js.getString("bookingdates.checkout"));
        System.out.println("Total Price INR " + js.getInt("totalprice"));
        System.out.println("Deposit status: " + (js.getBoolean("depositpaid") ? "Paid" : "Unpaid"));
        System.out.println("Additional Needs " + js.getString("additionalneeds"));
        System.out.println("------------------------------------------------------------------");

    }
}
