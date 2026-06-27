package com.izzatismail.mealmap.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual fun createSqlDriver(): SqlDriver {
    val context = requireNotNull(DbConfig.androidContext as? Context) { "Android Context not set in DbConfig" }
    return AndroidSqliteDriver(MealMapDatabase.Schema, context, "mealmap.db")
}