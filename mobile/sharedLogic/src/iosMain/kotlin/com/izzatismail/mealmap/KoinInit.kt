package com.izzatismail.mealmap

import com.izzatismail.mealmap.api.ApiConfig
import com.izzatismail.mealmap.api.IosTokenProvider
import com.izzatismail.mealmap.api.TokenProvider
import com.izzatismail.mealmap.di.sharedModule
import com.izzatismail.mealmap.repository.RecipeRepository
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
            return requireNotNull(koinInstance) {
                "Koin has not been initialized. Call IosKoinInitializer().setupKoin(baseUrl) first."
            }.get()
        }
    }
}