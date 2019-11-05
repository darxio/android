package com.darx.foodscaner.models

data class Ingredient (
    val name: String,
    val danger: Int = -1,
    val ingredients: ArrayList<Ingredient> = ArrayList<Ingredient>()

)

data class IngredientExtended (
    val name: String,
    val danger: Int,
    val description: String,
    val wiki_link: String,
    val ingredients: Ingredient
)