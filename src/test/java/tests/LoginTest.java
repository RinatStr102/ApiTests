package tests;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginTest {

    private final String baseUrl = "https://demoqa.com";
    private String token; // Переменная для хранения токена
    private String userId; // Переменная для хранения userId
    private final String isbn = "9781449325862"; // ISBN книги для добавления и удаления
    private RequestSpecification commonSpec; // Общая спецификация

    @BeforeEach
    @Step("Подготовка токена и спецификаций")
    void setup() {
        loginAndGetToken(); // Выполняем логин
        commonSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build(); // Создаем общую спецификацию для запросов
    }

    @Step("Логин и получение токена")
    void loginAndGetToken() {
        String requestBody = """
                {
                    "userName": "IlonMask",
                    "password": "Password123!"
                }
                """;

        Response response = given()
                .baseUri(baseUrl)
                .basePath("/Account/v1/Login")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("token", notNullValue()) // Проверяем, что токен не null
                .body("userId", notNullValue()) // Проверяем, что userId не null
                .extract().response();

        token = response.jsonPath().getString("token");
        userId = response.jsonPath().getString("userId");

        System.out.println("Token: " + token);
        System.out.println("UserId: " + userId);
    }

    @Step("Добавление книги с ISBN: {isbn} в профиль пользователя")
    void addBookToUserProfile(String isbn) {
        String requestBody = """
            {
                "userId": "%s",
                "collectionOfIsbns": [
                    { "isbn": "%s" }
                ]
            }
            """.formatted(userId, isbn);

        System.out.println("Request Body for Adding Book: " + requestBody);

        Response response = given()
                .spec(commonSpec)
                .basePath("/BookStore/v1/Books")
                .body(requestBody)
                .when()
                .post();

        // Логируем ответ
        response.then().log().all();

        // Проверяем статус и тело ответа
        response.then()
                .statusCode(201)
                .body("books[0].isbn", equalTo(isbn)); // Проверка ISBN
    }

    @Step("Удаление книги с ISBN: {isbn} из профиля пользователя")
    void deleteBookFromUserProfile(String isbn) {
        String deleteRequestBody = """
                {
                    "isbn": "%s",
                    "userId": "%s"
                }
                """.formatted(isbn, userId);

        System.out.println("Request Body for Deleting Book: " + deleteRequestBody);

        given()
                .spec(commonSpec)
                .basePath("/BookStore/v1/Book")
                .body(deleteRequestBody)
                .when()
                .delete()
                .then()
                .statusCode(204) // Успешное удаление
                .log().all();
    }

    @Test
    @Step("Тест: добавление и удаление книги")
    void addAndDeleteBookTest() {
        addBookToUserProfile(isbn); // Добавляем книгу в профиль
        deleteBookFromUserProfile(isbn); // Удаляем книгу из профиля

        // Проверяем, что книги больше нет в профиле
        given()
                .spec(commonSpec)
                .basePath("/Account/v1/User/" + userId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("books.size()", equalTo(0)) // Проверяем, что список книг пустой
                .log().all();
    }
}
