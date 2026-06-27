package com.izzatismail.mealmap

import android.app.Application
import com.izzatismail.mealmap.api.ApiConfig
import com.izzatismail.mealmap.database.DbConfig
import com.izzatismail.mealmap.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MealMapApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiConfig.baseUrl = BuildConfig.API_BASE_URL
        ApiConfig.isDebug = BuildConfig.DEBUG
        DbConfig.androidContext = this
        startKoin {
            androidContext(this@MealMapApp)
            modules(sharedModule)
        }
    }
}