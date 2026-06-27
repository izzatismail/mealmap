package com.izzatismail.mealmap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = ColorCardBg,
    primaryContainer = ColorPrimaryLight,
    onPrimaryContainer = ColorPrimary,
    secondary = ColorAccent,
    onSecondary = ColorCardBg,
    secondaryContainer = ColorAccentLight,
    onSecondaryContainer = ColorAccent,
    background = ColorBg,
    onBackground = ColorTextPrimary,
    surface = ColorSurface,
    onSurface = ColorTextPrimary,
    surfaceVariant = ColorCardBg,
    onSurfaceVariant = ColorTextSecondary,
    error = ColorError,
    onError = ColorCardBg,
    errorContainer = ColorErrorLight,
    onErrorContainer = ColorError,
    outline = ColorBorder,
    outlineVariant = ColorBorder,
)

private val MealMapShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp),
)

@Composable
fun MealMapTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = MealMapTypography,
        shapes = MealMapShapes,
        content = content,
    )
}