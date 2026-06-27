package com.izzatismail.mealmap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.izzatismail.mealmap.screens.RecipeDetailScreen
import com.izzatismail.mealmap.screens.RecipeListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.RecipeList,
    ) {
        composable<Screen.RecipeList> {
            RecipeListScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail(recipeId))
                }
            )
        }
        composable<Screen.RecipeDetail> { backStackEntry ->
            val detail: Screen.RecipeDetail = backStackEntry.toRoute()
            RecipeDetailScreen(
                recipeId = detail.recipeId,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}