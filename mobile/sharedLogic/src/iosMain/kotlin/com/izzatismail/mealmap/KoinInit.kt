package com.izzatismail.mealmap

import com.izzatismail.mealmap.api.ApiConfig
import com.izzatismail.mealmap.di.sharedModule
import com.izzatismail.mealmap.repository.RecipeRepository
import org.koin.core.Koin
import org.koin.core.context.startKoin

class IosKoinInitializer {
    fun setupKoin(baseUrl: String) {
        ApiConfig.baseUrl = baseUrl
        koinInstance = startKoin {
            modules(sharedModule)
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