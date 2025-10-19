package ru.practicum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.model.Order;
import ru.practicum.model.User;
import ru.practicum.steps.OrderSteps;
import ru.practicum.steps.UserSteps;
import java.util.*;
import static org.hamcrest.Matchers.*;


public class CreatingOrderTest extends BaseTest {

    private OrderSteps orderSteps;
    private UserSteps userSteps;
    private User user;
    private Map<String, Object> authToken = new HashMap<>();

    @Before
    public void setup() {
        orderSteps = new OrderSteps();
        userSteps = new UserSteps();

        String randomLocalPart = RandomStringUtils.randomAlphanumeric(8);
        String domain = "yandex.ru";

        user = new User();
        user.setEmail(randomLocalPart + "@" + domain);
        user.setName(RandomStringUtils.randomAlphabetic(8));
        user.setPassword(RandomStringUtils.randomAlphabetic(8));

        authToken = userSteps.registerUserAndGetTokens(user);
        orderSteps.setAccessToken((String) authToken.get("accessToken"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void сreateOrderWithAuthorizationTest() {

        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6c");
        Order order = new Order(ingredients);

        orderSteps.createOrder(order)
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", not(nullValue()));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthenticationTest() {
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6c");
        Order order = new Order(ingredients);

        orderSteps.setAccessToken("");
        orderSteps.createOrder(order)
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", containsString("Unauthorized"));
        // Баг на сервере, приходит другой ответ, отличный от ОР.
    }

    @Test
    @DisplayName("Создание заказа после авторизации без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(new ArrayList<>());

        orderSteps.createOrder(order)
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsTest() {
        List<String> invalidIngredients = Arrays.asList("invalid_hash_1", "invalid_hash_2");
        Order order = new Order(invalidIngredients);

        orderSteps.createOrder(order)
                .statusCode(500);
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
