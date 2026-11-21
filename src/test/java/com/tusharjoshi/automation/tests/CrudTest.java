package com.tusharjoshi.automation.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CrudTest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void createPost() {
        String requestbody = """
                {
                "title" : "API Testing",
                "body" : "Day1 with RESTASSURED",
                "userId" : 12234,
                "id":12234
                }
                """;

        given().contentType(ContentType.JSON)
                .body(requestbody)
                .log().all()
                .when()
                .post("/posts/")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("API Testing"))
                .body("userId", equalTo(12234));
    }

    @Test
    public void usePut() {
        String requestbody = """
                {
                "title" : "MyAPI Testing",
                "body" : "MyDay1 with RESTASSURED",
                "userId" : 12
                
                }
                """;
        given()
                .contentType(ContentType.JSON)
                .body(requestbody)
                .put("/posts/12")
                .then()
                .statusCode(200)
                .body("userId",equalTo(12))
                              .log().all();


    }

    @Test
    public  void  usePatch(){
        given()
                .contentType(ContentType.JSON)
                .body("{ \"title\": \"Patched MyAPI Testing\" }")
                .when()
                .patch("/posts/12234")
                .then()
                .statusCode(200)
                .body("title",equalTo("Patched MyAPI Testing"))
                .log().all();

    }
    @Test
    public  void  useDel(){
        given()
                .when()
                .delete("/posts/12234")
                .then()
                .statusCode(200);

        given()
                .when()
                 .then()
                .statusCode(200)
                .body("userid",equalTo(12234));

    }
}
