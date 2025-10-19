package ru.practicum.model;

import java.util.List;

public class CreatingOrder {

    private List<String> ingredients;

    public CreatingOrder (List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public CreatingOrder () {}

    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
