package com.izzatismail.mealmap.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createSqlDriver(): SqlDriver {
    return NativeSqliteDriver(MealMapDatabase.Schema, "mealmap.db")
}