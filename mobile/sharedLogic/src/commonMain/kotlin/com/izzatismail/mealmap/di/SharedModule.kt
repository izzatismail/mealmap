package com.izzatismail.mealmap.di

import com.izzatismail.mealmap.api.AuthApi
import com.izzatismail.mealmap.api.HttpClientFactory
import com.izzatismail.mealmap.api.MealMapApi
import com.izzatismail.mealmap.database.MealMapDatabase
import com.izzatismail.mealmap.database.createSqlDriver
import com.izzatismail.mealmap.repository.AuthRepository
import com.izzatismail.mealmap.repository.RecipeRepository
import com.izzatismail.mealmap.viewmodel.AuthViewModel
import com.izzatismail.mealmap.viewmodel.RecipeViewModel
import org.koin.dsl.module

val sharedModule = module {
    single { HttpClientFactory.create(tokenProvider = getOrNull()) }
    single { MealMapApi(get()) }
    single { AuthApi(get()) }
    single { MealMapDatabase(createSqlDriver()) }
    single { RecipeRepository(get(), get()) }
    single { AuthRepository(get(), get()) }
    single { RecipeViewModel(get()) }
    single { AuthViewModel(get()) }
}