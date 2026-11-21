package com.tusharjoshi.automation.base;
import com.tusharjoshi.automation.base.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.lessThan;

public class Specs {

    private static final String BASE_URI = "https://restful-booker.herokuapp.com";

    public static RequestSpecification request() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.uri"))
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addFilter(new AllureRestAssured())      // ← every call recorded in the report
                .log(LogDetail.URI)
                .build();
    }

    public static RequestSpecification authRequest(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(request())
                .addCookie("token", token)
                .build();
    }

    public static ResponseSpecification ok() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(5000L))
                .build();
    }
}