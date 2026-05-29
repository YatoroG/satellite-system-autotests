package base;

import framework.client.ApiClient;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;


import static framework.config.TestConfig.BASE_URL;

public class BaseTest {
    public static ApiClient apiClient;
    protected static Response lastResponse;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        apiClient = new ApiClient();
    }
}
