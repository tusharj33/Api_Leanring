package com.tusharjoshi.automation.tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PostsTest {

    @BeforeClass
    public void dataSetup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void getSinglePost() {
        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"))
                .body("id", equalTo(1));
    }

    @Test
    public void getCount() {
        given()
                .when()
                .get("/posts")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", equalTo(100));

    }

    @Test
    public void getCounts() {
        given()
                .queryParam("userID", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200);


    }

    @Test
    public void getParam() {
        given()
                .pathParam("userId", 2)
                .when()
                .get("/posts/{userId}")
                .then()
                .statusCode(lessThan(500))
                .log().status();
        Response response = given()
                .when()
                .get("/posts/1");

        String bodyField = response.jsonPath().getString("body");
        System.out.println(bodyField);
        Assert.assertEquals(response.getStatusCode(), 200);
        JsonPath jp = response.jsonPath();

        int id = jp.getInt("id");
        int userId = jp.getInt("userId");
        String title = jp.getString("title");
        bodyField = jp.getString("body");

        System.out.println("body: " + bodyField);

        Assert.assertEquals(id, 1);
        Assert.assertEquals(userId, 1);
        Assert.assertNotNull(title);
        Assert.assertTrue(bodyField.startsWith("1quia et suscipit"));

    }

    @Test
    public void getMydata() {
        Response response = given()
                .when()
                .get("/posts/");
        JsonPath js = response.jsonPath();
        Map<Integer, Integer> myMap = new HashMap<Integer, Integer>();

        List<Integer> ids = js.getList("id", Integer.class);
        List<Integer> userIds = js.getList("userId", Integer.class);
        System.out.println(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            myMap.put(userIds.get(i), ids.get(i));
        }
        System.out.println(myMap.size());


    }

}
