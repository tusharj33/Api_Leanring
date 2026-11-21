package com.tusharjoshi.automation.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
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
    // Each class owns its spec, so classes can run in parallel without
    // overwriting a shared static RestAssured.baseURI
    private RequestSpecification spec;

    @BeforeClass
    public void setup() {
        spec = new RequestSpecBuilder().setBaseUri("https://jsonplaceholder.typicode.com").build();   // per-class spec: no shared global baseURI
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

        given().spec(spec).contentType(ContentType.JSON)
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
        given().spec(spec)
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
        given().spec(spec)
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
        given().spec(spec)
                .when()
                .delete("/posts/12234")
                .then()
                .statusCode(200);

        given().spec(spec)
                .when()
                 .then()
                .statusCode(200)
                .body("userid",equalTo(12234));

    }
}
