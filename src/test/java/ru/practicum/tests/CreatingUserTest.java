package ru.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.model.User;
import ru.practicum.steps.UserSteps;
import java.util.Map;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_ACCEPTED;


public class CreatingUserTest extends BaseTest {

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
    }

    @Test
    @DisplayName("Регистрация нового пользователя и получение токенов")
    @Description("Проверяется успешная регистрация нового пользователя и выдача аутентификационных токенов")
    public void registerUserAndGetTokensTest() {
        authToken = userSteps.registerUserAndGetTokens(user);

        Assert.assertNotNull(authToken.get("accessToken"));
        var response = userSteps.getLastResponse();
        Assert.assertEquals(SC_OK, response.getStatusCode());
        response.then().assertThat().body("success", is(equalTo(true)));
    }

    @Test
    @DisplayName("Повторная попытка регистрации существующего пользователя")
    @Description("Проверяется попытку повторно создать учетную запись с существующими данными пользователя")
    public void duplicateRegistrationTest() {
        userSteps.sendRegisterRequest(user);
        userSteps.sendRegisterRequest(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без электронной почты")
    @Description("Проверяется ошибка при попытке зарегистрировать пользователя без обязательного поля 'email'")
    public void createUserWithoutEmailTest() {
        User invalidUser = new User();
        invalidUser.setName(user.getName());
        invalidUser.setPassword(user.getPassword());

        userSteps.sendRegisterRequest(invalidUser)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без пароля")
    @Description("Проверяется ошибка при попытке зарегистрировать пользователя без обязательного поля 'password'")
    public void createUserWithoutPasswordTest() {
        User invalidUser = new User();
        invalidUser.setEmail(user.getEmail());
        invalidUser.setName(user.getName());

        userSteps.sendRegisterRequest(invalidUser)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя без имени")
    @Description("Проверяется ошибка при попытке зарегистрировать пользователя без обязательного поля 'name'")
    public void createUserWithoutNameTest() {
        User invalidUser = new User();
        invalidUser.setEmail(user.getEmail());
        invalidUser.setPassword(user.getPassword());

        userSteps.sendRegisterRequest(invalidUser)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Удаление зарегистрированного пользователя")
    @Description("Проверяется удаление ранее зарегистрированного пользователя и подтверждение успешной операции")
    public void deleteRegisteredUserTest() {
        authToken = userSteps.registerUserAndGetTokens(user);
        userSteps.setAccessToken((String) authToken.get("accessToken"));
        userSteps.deleteUser()
                .then()
                .statusCode(SC_ACCEPTED)
                .body("success", equalTo(true));
    }

    @After
    @DisplayName("Удаление зарегистрированных пользователей")
    public void cleanUpUsers() {
        if (authToken != null && !authToken.isEmpty()) {
            userSteps.setAccessToken((String) authToken.get("accessToken"));
            userSteps.deleteUser();
        }
    }
}