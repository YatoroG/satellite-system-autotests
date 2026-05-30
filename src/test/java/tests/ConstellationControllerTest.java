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
public class ConstellationControllerTest extends BaseTest {
    private Long createdConstellationId;
    private Long createdSatelliteId;
    private final String constellationName = "Test-Constellation";

    @Test
    @Order(1)
    @DisplayName("[201] POST /api/constellations - Создание новой группировки")
    void createConstellationTest() {
        Map<String, Object> requestBody = Map.of("name", constellationName);

        Response response = apiClient.post("/api/constellations", requestBody);
        response.then().spec(expectedStatusCode(201))
                .body("id", notNullValue())
                .body("constellationName", equalTo(constellationName));

        createdConstellationId = response.jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    @DisplayName("[200] GET /api/constellations - Получение списка всех группировок")
    void getAllConstellationsTest() {
        apiClient.get("/api/constellations").then().spec(expectedStatusCode(200)).body("$", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("[200] GET /api/constellations/{id} - Получение группировки по ID")
    void getConstellationByIdTest() {
        apiClient.get("/api/constellations/" + createdConstellationId)
                .then().spec(expectedStatusCode(200))
                .body("id", equalTo(createdConstellationId.intValue()))
                .body("constellationName", equalTo(constellationName));
    }

    @Test
    @Order(4)
    @DisplayName("[200] POST /api/constellations/{id}/satellites - Добавление спутника в группировку")
    void addSatelliteToConstellationTest() {
        Map<String, Object> satelliteBody = Map.of(
                "name", "Comm-1",
                "type", "COMMUNICATION"
        );

        Response satResponse = apiClient.post("/api/satellites", satelliteBody);
        createdSatelliteId = satResponse.jsonPath().getLong("id");

        Map<String, Object> requestBody = Map.of("satelliteId", createdSatelliteId);
        apiClient.post("/api/constellations/" + createdConstellationId + "/satellites", requestBody)
                .then().spec(expectedStatusCodeWithoutResponse(200));
    }

    @Test
    @Order(5)
    @DisplayName("[204] DELETE /api/constellations/{id} - Удаление группировки из системы")
    void deleteConstellationTest() {
        apiClient.delete("/api/constellations/" + createdConstellationId)
                .then().spec(expectedStatusCodeWithoutResponse(204));
    }

    @Test
    @Order(6)
    @DisplayName("[404] GET /api/constellations/{id} - Ошибка при запросе удаленной группировки")
    void getConstellationNotFoundTest() {
        apiClient.get("/api/constellations/" + createdConstellationId)
                .then().spec(expectedStatusCodeWithoutResponse(404));
    }

    @AfterAll
    void tearDown() {
        if (createdSatelliteId != null) {
            apiClient.delete("/api/satellites/" + createdSatelliteId);
        }

        if (createdConstellationId != null) {
            apiClient.delete("/api/constellations/" + createdConstellationId);
        }
    }
}
