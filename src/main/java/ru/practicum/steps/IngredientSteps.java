package ru.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class IngredientSteps {

    public static final String INGREDIENTS_ENDPOINT = "api/ingredients";

    @Step("Получаем список доступных ингредиентов")
    public ValidatableResponse getAvailableIngredients() {
        return given()
                .get(INGREDIENTS_ENDPOINT)
                .then();
    }
}
