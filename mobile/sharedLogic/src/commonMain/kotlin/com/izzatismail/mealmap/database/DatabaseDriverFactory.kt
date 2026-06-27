package com.izzatismail.mealmap.database

object DbConfig {
    var androidContext: Any? = null
}

expect fun createSqlDriver(): app.cash.sqldelight.db.SqlDriver