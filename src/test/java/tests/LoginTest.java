package tests;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginTest {

    @Test
    void successfulLogin() {
        String requestBody = "{\"username\": \"IlonMask\", \"password\":\"Password123!\"}";

        // Отправим POST
    given()
            .baseUri("https://demoqa.com")
            .basePath("/login")
            .header("Content-Type", "application/json")
            .body(requestBody)
            .when()
            .post()
            .then()
            .statusCode(200)
            .body("token", equalTo("expected_token"));
    }
}
