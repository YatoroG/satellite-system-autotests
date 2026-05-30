package tests;

import base.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static framework.specs.ResponseSpecification.expectedStatusCode;
import static framework.specs.ResponseSpecification.expectedStatusCodeWithoutResponse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnergySystemControllerTest extends BaseTest {
    private Long generatedId;

    @Test
    @Order(1)
    @DisplayName(" [201] POST /api/satellites - Создание спутника для генерации энергосистемы")
    void prepareDataTest() {
        Map<String, Object> satelliteBody = Map.of("name", "Comm-1", "type", "COMMUNICATION");
        Response response = apiClient.post("/api/satellites", satelliteBody);
        response.then().spec(expectedStatusCode(201));
        generatedId = response.jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    @DisplayName("[200] GET /api/energy-systems - Получение списка всех энергосистем")
    void getAllEnergySystemsTest() {
        apiClient.get("/api/energy-systems")
                .then().spec(expectedStatusCode(200))
                .body("$", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("[200] GET /api/energy-systems/{id} - Получение энергосистемы по ID")
    void getEnergySystemByIdTest() {
        apiClient.get("/api/energy-systems/" + generatedId)
                .then().spec(expectedStatusCode(200))
                .body("id", org.hamcrest.Matchers.equalTo(generatedId.intValue()));
    }

    @Test
    @Order(4)
    @DisplayName("[200] PUT /api/energy-systems/{id} - Обновление параметров энергосистемы")
    void updateEnergySystemTest() {
        Map<String, Object> requestBody = Map.of(
                "batteryLevel", 85,
                "lowBatteryThreshold", 20,
                "maxBattery", 100,
                "minBattery", 0
        );

        apiClient.put("/api/energy-systems/" + generatedId, requestBody)
                .then()
                .spec(expectedStatusCode(200))
                .body("batteryLevel", org.hamcrest.Matchers.equalTo(85.0f))
                .body("lowBatteryThreshold", org.hamcrest.Matchers.equalTo(20.0f))
                .body("maxBattery", org.hamcrest.Matchers.equalTo(100.0f))
                .body("minBattery", org.hamcrest.Matchers.equalTo(0.0f));
    }

    @Test
    @Order(5)
    @DisplayName("[404] GET /api/energy-systems/{id} - Ошибка при запросе несуществующей энергосистемы")
    void getEnergySystemNotFoundTest() {
        long nonExistentId = 999999L;
        apiClient.get("/api/energy-systems/" + nonExistentId)
                .then()
                .spec(expectedStatusCodeWithoutResponse(404));
    }

    @AfterAll
    void tearDown() {
        if (generatedId != null) {
            apiClient.delete("/api/satellites/" + generatedId);
        }
    }
}
