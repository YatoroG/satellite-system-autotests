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
    private Integer energySystemId;

    @Test
    @Order(1)
    @DisplayName("[200] GET /api/energy-systems - Получение списка всех энергосистем")
    void getAllEnergySystemsTest() {
        Response response = apiClient.get("/api/energy-systems");
        response.then().spec(expectedStatusCode(200)).body("$", notNullValue());

        try {
            energySystemId = response.jsonPath().getInt("[0].id");
        } catch (Exception e) {
            energySystemId = 1;
        }
    }

    @Test
    @Order(2)
    @DisplayName("[200] GET /api/energy-systems/{id} - Получение энергосистемы по ID")
    void getEnergySystemByIdTest() {
        Response response = apiClient.get("/api/energy-systems/" + energySystemId);
        response.then().spec(expectedStatusCode(200)).body("id", org.hamcrest.Matchers.equalTo(energySystemId));
    }

    @Test
    @Order(3)
    @DisplayName("[200] PUT /api/energy-systems/{id} - Обновление параметров энергосистемы")
    void updateEnergySystemTest() {
        Map<String, Object> requestBody = Map.of(
                "batteryLevel", 85,
                "lowBatteryThreshold", 20,
                "maxBattery", 100,
                "minBattery", 0
        );

        apiClient.put("/api/energy-systems/" + energySystemId, requestBody)
                .then().spec(expectedStatusCode(200))
                .body("batteryLevel", org.hamcrest.Matchers.equalTo(85));
    }

    @Test
    @Order(4)
    @DisplayName("[404] GET /api/energy-systems/{id} - Ошибка при запросе несуществующей энергосистемы")
    void getEnergySystemNotFoundTest() {
        long nonExistentId = 999999L;
        apiClient.get("/api/energy-systems/" + nonExistentId).then().spec(expectedStatusCodeWithoutResponse(404));
    }
}
