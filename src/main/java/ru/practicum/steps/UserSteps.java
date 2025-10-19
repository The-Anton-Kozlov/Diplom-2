package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
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

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    @Step("Отправка запроса на регистрацию пользователя")
    public ValidatableResponse sendRegisterRequest(User user) {
        return given()
                .body(user)
                .post(USER_CREATE)
                .then();
    }

    @Step("Регистрация нового пользователя и возврат токенов")
    public Map<String, Object> registerUserAndGetTokens(User user) {
        ValidatableResponse response = sendRegisterRequest(user);
        response.statusCode(200);

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", response.extract().path("accessToken"));
        tokens.put("refreshToken", response.extract().path("refreshToken"));
        return tokens;
    }


    @Step("Авторизация пользователя")
    public ValidatableResponse userLogin(UserLogin userLogin) {
        return given()
                .body(userLogin)
                .post(USER_LOGIN)
                .then();
    }


    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser() {
        return given()
                .header("Authorization", this.accessToken)
                .delete(USER_DELETE)
                .then();
    }
}