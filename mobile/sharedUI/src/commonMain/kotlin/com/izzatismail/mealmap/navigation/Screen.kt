package com.izzatismail.mealmap.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object RecipeList : Screen()

    @Serializable
    data class RecipeDetail(val recipeId: Long) : Screen()
}