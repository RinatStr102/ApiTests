package tests;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTest {

    @Test
    void successfulLogin() {
        String requestBody = "{\"userName\": \"IlonMask\", \"password\": \"Password123!\"}";

        // Отправим POST-запрос
        given()
                .baseUri("https://demoqa.com")
                .basePath("/Account/v1/Login")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200) // ожидаем статус 200 OK
                .body("token", notNullValue()) // проверяем, что токен возвращается
                .log().all(); // логируем ответ для наглядности
    }
}
