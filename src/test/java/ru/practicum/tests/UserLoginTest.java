package ru.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.model.User;
import ru.practicum.model.UserLogin;
import ru.practicum.steps.UserSteps;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;


public class UserLoginTest extends BaseTest {

    private UserSteps userSteps = new UserSteps();
    private User user;
    private Map<String, Object> authToken;

    @Before
    public void setUp() {
        String randomLocalPart = RandomStringUtils.randomAlphanumeric(8);
        String domain = "yandex.ru";

        user = new User();
        user.setEmail(randomLocalPart + "@" + domain);
        user.setName(RandomStringUtils.randomAlphabetic(8));
        user.setPassword(RandomStringUtils.randomAlphabetic(8));

        authToken = userSteps.registerUserAndGetTokens(user);
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка успешной авторизации существующего пользователя")
    public void userLogin() {
        UserLogin correctCredentials = new UserLogin(user.getEmail(), user.getPassword());
        userSteps
                .userLogin(correctCredentials).then()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Проверка реакции сервера на попытки авторизации с неправильным email")
    public void userLoginWithWrongEmail() {
        String wrongEmail = RandomStringUtils.randomAlphanumeric(8) + "@example.com";
        UserLogin incorrectCredentials = new UserLogin(wrongEmail, user.getPassword());

        userSteps
                .userLogin(incorrectCredentials).then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка реакции сервера на попытки авторизации с неправильным паролем")
    public void userLoginWithWrongPassword() {
        String wrongPassword = RandomStringUtils.randomAlphabetic(12);
        UserLogin incorrectCredentials = new UserLogin(user.getEmail(), wrongPassword);

        userSteps
                .userLogin(incorrectCredentials).then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    @DisplayName("Удаление созданных пользователей")
    public void cleanUpUsers() {
        if (authToken != null && !authToken.isEmpty()) {
            userSteps.setAccessToken(authToken.get("accessToken").toString());
            userSteps.deleteUser();
        }
    }
}
