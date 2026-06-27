# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.izzatismail.mealmap.**$$serializer { *; }
-keepclassmembers class com.izzatismail.mealmap.** {
    *** Companion;
}
-keepclasseswithmembers class com.izzatismail.mealmap.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Koin
-keep class org.koin.** { *; }

# SQLDelight
-keep class app.cash.sqldelight.** { *; }
-keep class co.touchlab.** { *; }

# Compose
-keep class androidx.compose.** { *; }