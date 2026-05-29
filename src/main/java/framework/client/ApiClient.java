package framework.client;

import java.util.Map;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;

public class ApiClient {
    public Response get(String endpoint) {
        return given()
                .log().all()
                .then()
                .log().all()
                .when()
                .get(endpoint);
    }

    public Response get(
            String endpoint,
            Map<String, Object> queryParams
    ) {
        return given()
                .log().all()
                .queryParams(queryParams)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response post(
        String endpoint,
        Map<String, Object> body
    ) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .then()
                .log().all()
                .when()
                .post(endpoint);
    }

    public Response patch(
        String endpoint,
        Map<String, Object> body
    ) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .then()
                .log().all()
                .when()
                .patch(endpoint);
    }

    public Response delete(String endpoint) {
        return given()
                .log().all()
                .when()
                .delete(endpoint)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response put(
            String endpoint,
            Map<String, Object> body
    ) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .then()
                .log().all()
                .when()
                .patch(endpoint);
    }
}
