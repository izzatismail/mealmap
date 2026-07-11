package com.izzatismail.mealmap.di

import com.izzatismail.mealmap.api.AuthApi
import com.izzatismail.mealmap.api.FavoriteApi
import com.izzatismail.mealmap.api.HttpClientFactory
import com.izzatismail.mealmap.api.MealMapApi
import com.izzatismail.mealmap.api.MealPlanApi
import com.izzatismail.mealmap.api.PantryApi
import com.izzatismail.mealmap.api.ShoppingListApi
import com.izzatismail.mealmap.database.MealMapDatabase
import com.izzatismail.mealmap.database.createSqlDriver
import com.izzatismail.mealmap.repository.AuthRepository
import com.izzatismail.mealmap.repository.RecipeRepository
import com.izzatismail.mealmap.viewmodel.AuthViewModel
import com.izzatismail.mealmap.viewmodel.FavoriteViewModel
import com.izzatismail.mealmap.viewmodel.MealPlanViewModel
import com.izzatismail.mealmap.viewmodel.PantryViewModel
import com.izzatismail.mealmap.viewmodel.RecipeViewModel
import com.izzatismail.mealmap.viewmodel.ShoppingListViewModel
import org.koin.dsl.module

val sharedModule = module {
    single { HttpClientFactory.create(tokenProvider = getOrNull()) }
    single { MealMapApi(get()) }
    single { AuthApi(get()) }
    single { FavoriteApi(get()) }
    single { MealPlanApi(get()) }
    single { ShoppingListApi(get()) }
    single { PantryApi(get()) }
    single { MealMapDatabase(createSqlDriver()) }
    single { RecipeRepository(get(), get()) }
    single { AuthRepository(get(), get()) }
    single { RecipeViewModel(get()) }
    single { AuthViewModel(get()) }
    single { FavoriteViewModel(get()) }
    single { MealPlanViewModel(get()) }
    single { ShoppingListViewModel(get()) }
    single { PantryViewModel(get()) }
}