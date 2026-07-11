package com.izzatismail.mealmap.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object Main : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object RecipeList : Screen()

    @Serializable
    data class RecipeDetail(val recipeId: Long) : Screen()

    @Serializable
    data object Planner : Screen()

    @Serializable
    data object ShoppingList : Screen()

    @Serializable
    data object Pantry : Screen()
}