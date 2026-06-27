package com.izzatismail.mealmap

import com.izzatismail.mealmap.api.ApiConfig
import com.izzatismail.mealmap.di.sharedModule
import org.koin.core.context.startKoin

class IosKoinInitializer {
    fun setupKoin(baseUrl: String) {
        ApiConfig.baseUrl = baseUrl
        startKoin {
            modules(sharedModule)
        }
    }
}