package com.izzatismail.mealmap

import androidx.compose.runtime.Composable
import com.izzatismail.mealmap.navigation.AppNavigation
import com.izzatismail.mealmap.ui.theme.MealMapTheme

@Composable
fun App() {
    MealMapTheme {
        AppNavigation()
    }
}