package com.izzatismail.mealmap

import com.izzatismail.mealmap.api.ApiConfig
import com.izzatismail.mealmap.api.IosTokenProvider
import com.izzatismail.mealmap.api.TokenProvider
import com.izzatismail.mealmap.di.sharedModule
import com.izzatismail.mealmap.repository.RecipeRepository
import com.izzatismail.mealmap.viewmodel.AuthViewModel
import com.izzatismail.mealmap.viewmodel.FavoriteViewModel
import com.izzatismail.mealmap.viewmodel.MealPlanViewModel
import com.izzatismail.mealmap.viewmodel.PantryViewModel
import com.izzatismail.mealmap.viewmodel.ShoppingListViewModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module

class IosKoinInitializer {
    fun setupKoin(baseUrl: String) {
        ApiConfig.baseUrl = baseUrl
        koinInstance = startKoin {
            modules(sharedModule + module {
                single<TokenProvider> { IosTokenProvider() }
            })
        }.koin
    }

    companion object {
        private var koinInstance: Koin? = null

        fun provideRecipeRepository(): RecipeRepository {
            return requireNotNull(koinInstance) { "Koin not initialized" }.get()
        }

        fun provideAuthViewModel(): AuthViewModel {
            return requireNotNull(koinInstance) { "Koin not initialized" }.get()
        }

        fun provideFavoriteViewModel(): FavoriteViewModel {
            return requireNotNull(koinInstance) { "Koin not initialized" }.get()
        }

        fun provideMealPlanViewModel(): MealPlanViewModel {
            return requireNotNull(koinInstance) { "Koin not initialized" }.get()
        }

        fun provideShoppingListViewModel(): ShoppingListViewModel {
            return requireNotNull(koinInstance) { "Koin not initialized" }.get()
        }

        fun providePantryViewModel(): PantryViewModel {
            return requireNotNull(koinInstance) { "Koin not initialized" }.get()
        }
    }
}