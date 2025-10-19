package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.practicum.model.Order;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    public static final String ORDERS_ENDPOINT = "/api/orders";

    private String accessToken;

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    @Step("Создаем заказ с указанными ингредиентами")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .header("Authorization",  this.accessToken)
                .contentType("application/json")
                .body(order)
                .post(ORDERS_ENDPOINT)
                .then();
    }

    @Step("Создаем заказ без авторизации")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .post(ORDERS_ENDPOINT)
                .then();
    }

    @Step("Создаем заказ с неверными ингредиентами")
    public ValidatableResponse createOrderWithInvalidIngredients(List<String> invalidIngredients) {
        Order order = new Order(invalidIngredients);
        return given()
                .header("Authorization",  this.accessToken)
                .contentType("application/json")
                .body(order)
                .post(ORDERS_ENDPOINT)
                .then();
    }

    @Step("Создаем заказ без ингредиентов")
    public ValidatableResponse createOrderWithoutIngredients() {
        Order emptyOrder = new Order(new ArrayList<>());
        return given()
                .header("Authorization",  this.accessToken)
                .contentType("application/json")
                .body(emptyOrder)
                .post(ORDERS_ENDPOINT)
                .then();
    }
}

