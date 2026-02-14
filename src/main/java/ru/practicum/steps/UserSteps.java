package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.practicum.model.User;
import ru.practicum.model.UserLogin;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class UserSteps {

    public static final String USER_CREATE = "/api/auth/register";
    public static final String USER_LOGIN = "/api/auth/login";
    public static final String USER_DELETE = "/api/auth/user";

    private String accessToken;
    private Response lastResponse;

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    @Step("Отправка запроса на регистрацию пользователя")
    public Response  sendRegisterRequest(User user) {
        this.lastResponse = given()
                .body(user)
                .when()
                .post(USER_CREATE);
        return this.lastResponse;
    }

    public Response getLastResponse() {
        return this.lastResponse;
    }

    @Step("Регистрация нового пользователя и возврат токенов")
    public Map<String, Object> registerUserAndGetTokens(User user) {
        Response response = sendRegisterRequest(user);
        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            throw new AssertionError("Неверный статус HTTP: ожидалось 200, получено " + statusCode);
        }

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", response.path("accessToken"));
        tokens.put("refreshToken", response.path("refreshToken"));
        return tokens;
    }


    @Step("Авторизация пользователя")
    public Response userLogin(UserLogin userLogin) {
        return given()
                .body(userLogin)
                .when()
                .post(USER_LOGIN);
    }


    @Step("Удаление пользователя")
    public Response deleteUser() {
        return given()
                .header("Authorization", this.accessToken)
                .when()
                .delete(USER_DELETE);
    }
}