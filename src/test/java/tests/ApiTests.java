package tests;

import models.UsersResponse;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiTests {

    @Test
    void checkUsersApiResponseStatus() {
        // Step 1: Описываем тестовую ситуацию (Given - Дано)
        given()
                .log().all() // Логируем все, чтобы видеть, какие данные отправляются

                // Step 2: Отправляем запрос (When - Когда)
                .when()
                .get("https://reqres.in/api/users?page=2") // Выполняем GET-запрос на указанный URL

                // Step 3: Проверяем ответ (Then - Тогда)
                .then()
                .log().all() // Логируем ответ, чтобы видеть, что вернул сервер
                .statusCode(200); // Проверяем, что статус-код ответа равен 200
    }

    @Test
    void checkUsersPage1() {
        // Step 1: Описываем начальные условия (Given)
        given()
                .log().all() // логируем всё,что отправялется в запросе
        // Step 2: Выполняем Get-запрос (When)
                .when()
                .get("https://reqres.in/api/users?page=1")// выполняем запрос к первому ресурсу API
        // Step 3: Проверяем результат (Then)
                .then()
                .log().all() // логируем всё, что вернулось в ответе
                .statusCode(200) // Проверяем, что статус-код ответа — 200
                .body("page", equalTo(1)); // Убедимся, что это действительно первая страница
    }

    @Test
    void checkUserData() {
        // Step 1: Описываем начальные условия
        given()
                .log().all() // Логируем запрос

                // Step 2: Выполняем GET-запрос на конкретного пользователя
                .when()
                .get("https://reqres.in/api/users/2")

                // Step 3: Проверяем статус-код и данные в ответе
                .then()
                .log().all() // Логируем ответ
                .statusCode(200) // Убедимся, что статус-код — 200
                .body("data.first_name", equalTo("Janet")) // Проверяем имя пользователя
                .body("data.last_name", equalTo("Weaver")); // Проверяем фамилию пользователя
    }

    @Test
    void checkUserEmail() {
        given()
                .log().all()
                .when()
                .get("https://reqres.in/api/users/2")
                .then()
                .body("data.email",equalTo("janet.weaver@reqres.in"));
    }

    @Test
    void createUser() {
        //Описываем тело запроса
        String requestBody = "{\"name\":\"Ринат\",\"job\":\"QA\"}";

        //Выполняем post-запрос
        given()
                .log().all() // логируем весь запрос для проверки
                .header("Content-Type", "application/json") // указываем, что отправляем json
                .body(requestBody) // проверяем тело запроса
                .when()
                .post("https://reqres.in/api/users") // выполняем post запрос
                .then()
                .log().all() // логируем весь ответ для анализа
                .statusCode(201) // Проверяем, что статус-код равен 201 (Created)
                .body("name",equalTo("Ринат")) // Проверяем, что в ответе есть имя "morpheus"
                .body("job", equalTo("QA"));
    }

    @Test
    void updateUsers() {
        String requestBody = "{\"name\":\"Ринат\",\"job\":\"Automation Engineer\"}";
        // Step 2: Выполняем PUT-запрос
        given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("https://reqres.in/api/users/2")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", equalTo("Ринат"))
                .body("job", equalTo("Automation Engineer"));

    }

    @Test
    void updateUserJob() {
        // тело запроса для обновления только поля "job"
        String requestBody = "{\"job\":\"Senior QA Engineer\"}";

        //Step 1: Выполняем PATCH-запрос
        given()
                .log().all()
                .header("Content-Type", "application/json") // Указываем тип данных JSON
                .body(requestBody) // Передаём тело запроса с обновлением поля "job"
                .when()
                .patch("https://reqres.in/api/users/2") // Отправляем PATCH-запрос на обновление пользователя с id=2
                .then()
                .log().all() // Логируем ответ
                .statusCode(200) // Проверяем, что статус-код ответа — 200
                .body("job", equalTo("Senior QA Engineer")); // Проверяем, что "job" обновилось на "Senior QA Engineer"
    }

    @Test
    void deleteUser() {
        // Step 1: Выполняем DELETE-запрос
        given()
                .log().all() // Логируем запрос
                .when()
                .delete("https://reqres.in/api/users/2") // Отправляем DELETE-запрос на удаление пользователя с id=2
                .then()
                .log().all()
                .statusCode(204); // Проверяем, что статус-код — 204 (No Content), что означает успешное удаление
    }

    @Test
    void parseUsersResponse() {
        // Выполняем GET-запрос и парсим ответ в модель UsersResponse
        UsersResponse response = given()
                .log().all()
                .when()
                .get("https://reqres.in/api/users?page=2")
                .then()
                .log().all()
                .statusCode(200) // Проверяем, что статус-код 200
                .extract().as(UsersResponse.class); // Парсим ответ в объект UsersResponse

        // Проверяем парсинг ответа
        assert response.getPage() == 2; // Убедимся, что текущая страница — 2
        assert response.getData().size() > 0; // Убедимся, что список пользователей не пустой

        // Проверяем данные первого пользователя
        System.out.println("Первый пользователь: " + response.getData().get(0).getFirst_name());
        assert response.getData().get(0).getEmail().contains("@"); // Убедимся, что email валиден

    }
}