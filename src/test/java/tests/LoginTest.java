package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTest {

    private String baseUrl = "https://demoqa.com";
    private String token; // Переменная для хранения токена
    private String userId; // Переменная для хранения userId

    //Метод для получения токена и userId
    void loginAndGetToken() {
        String requestBody = "{\"userName\": \"IlonMask\", \"password\": \"Password123!\"}";

        Response response = given()
                .baseUri(baseUrl)
                .basePath("/Account/v1/Login")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("token",notNullValue()) // Проверяем, что токен не null
                .body("userId", notNullValue()) // Проверяем, что userId не null
                .extract().response();

        token = response.jsonPath().getString("token");
        userId = response.jsonPath().getString("userId");

        System.out.println("Token: " + token);
        System.out.println("UserId: " + userId);
    }
    // успешный логин
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
    // Добавление книги в профиль пользователя
    @Test
    void addBookToUserProfile() {
        loginAndGetToken(); // Получаем токен и userId перед тестом

        String requestBody = """
                {
                      "userId": "%s",
                      "collectionOfIsbns": [
                        { "isbn": "9781449325862" }
                      ]
                    }
                    """.formatted(userId);
        // Выполняем post запрос
        given()
                .baseUri(baseUrl) // Базовый URL
                .basePath("/BookStore/v1/Books") // Путь API
                .header("Content-Type", "application/json") // Указываем тип данных
                .header("Authorization", "Bearer " + token) // Передаем токен авторизации
                .body(requestBody) // JSON-запрос
                .when()
                .post()// post-запрос
                .then()
                .statusCode(201) //Проверям, что книга добавлена
                .body("books[0].isbn", equalTo("9781449325862")) // Проверяем ISBN
                .log().all(); //Логируемм ответ
    }
}
