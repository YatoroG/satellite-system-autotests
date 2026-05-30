package tests;

import java.util.Map;
import base.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;


import static framework.specs.ResponseSpecification.expectedStatusCode;
import static framework.specs.ResponseSpecification.expectedStatusCodeWithoutResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SatelliteControllerTest extends BaseTest {
    private Long createdSatelliteId;

    private Map<String, Object> createSatelliteParam(String name, String type) {
        return Map.of("name", name, "type", type);
    }

    @Test
    @Order(1)
    @DisplayName("[201] POST /api/satellites - Создание нового спутника")
    void createSatelliteTest() {
        Map<String, Object> requestBody = createSatelliteParam("Comm-1", "COMMUNICATION");

        Response response = apiClient.post("/api/satellites", requestBody);
        response.then().spec(expectedStatusCode(201)).body("id", notNullValue());

        createdSatelliteId = response.jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    @DisplayName("[200] GET /api/satellites - Получение списка всех спутников")
    void getAllSatellitesTest() {
        Response response = apiClient.get("/api/satellites");
        response.then().spec(expectedStatusCode(200)).body("$", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("[200] GET /api/satellites/{id} - Получение спутника по его ID")
    void getSatelliteByIdTest() {
        Response response = apiClient.get("/api/satellites/" + createdSatelliteId);
        response.then().spec(expectedStatusCode(200))
                .body("id", equalTo(createdSatelliteId.intValue()));
    }

    @Test
    @Order(4)
    @DisplayName("[200] PUT /api/satellites/{id} - Обновление данных спутника")
    void updateSatelliteTest() {
        String newName = "Updated-Comm-1";
        Map<String, Object> requestBody = Map.of("satelliteName", newName);

        Response response = apiClient.put("/api/satellites/" + createdSatelliteId, requestBody);
        response.then().spec(expectedStatusCode(200))
                .body("name", equalTo(newName));
    }

    @Test
    @Order(5)
    @DisplayName("[204] DELETE /api/satellites/{id} - Удаление спутника из системы")
    void deleteSatelliteTest() {
        Response response = apiClient.delete("/api/satellites/" + createdSatelliteId);
        response.then().spec(expectedStatusCodeWithoutResponse(204));
    }

    @Test
    @Order(6)
    @DisplayName("[404] GET /api/satellites/{id} - Ошибка при запросе несуществующего спутника")
    void getSatelliteByIdNotFoundTest() {
        Response response = apiClient.get("/api/satellites/" + createdSatelliteId);
        response.then().spec(expectedStatusCodeWithoutResponse(404));
    }

    @AfterAll
    void tearDown() {
        if (createdSatelliteId != null) {
            apiClient.delete("/api/satellites/" + createdSatelliteId);
        }
    }
}
