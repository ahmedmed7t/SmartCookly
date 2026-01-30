package com.nexable.smartcookly.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Custom Material3 ColorScheme for Light Theme
 * Using cooking/food-inspired color palette
 */

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF16664A),//#15eca3
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF16cb8a).copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFF1F2D2A),

    secondary = Color(0xFF9ED6C0),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF9ED6C0).copy(alpha = 0.25f),
    onSecondaryContainer = Color(0xFF78C3A6),

    tertiary = Color(0xFFF2C94C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2C94C).copy(alpha = 0.25f),
    onTertiaryContainer = Color(0xFF1F2D2A),

    background = Color(0xFFf6f8f7),
    onBackground = Color(0xFF1F2D2A),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2D2A),
    surfaceVariant = Color(0xFFEEF2EE),
    onSurfaceVariant = Color(0xFF5F6F6B),

    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFD32F2F).copy(alpha = 0.2f),
    onErrorContainer = Color(0xFFD32F2F),

    outline = Color(0xFFD6DED9),
    outlineVariant = Color(0xFFE3E9E6),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF1F2D2A),
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFF4CAF50),

    surfaceTint = Color(0xFF1E7F5C),
)


/**
 * Custom Material3 ColorScheme for Dark Theme
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF35B48A),
    onPrimary = Color(0xFF0F1B18),
    primaryContainer = Color(0xFF2B9C76).copy(alpha = 0.25f),
    onPrimaryContainer = Color(0xFFEAF4F1),

    secondary = Color(0xFF78C3A6),
    onSecondary = Color(0xFF0F1B18),
    secondaryContainer = Color(0xFF78C3A6).copy(alpha = 0.25f),
    onSecondaryContainer = Color(0xFF5EAE91),

    tertiary = Color(0xFFF2C94C),
    onTertiary = Color(0xFF0F1B18),
    tertiaryContainer = Color(0xFFF2C94C).copy(alpha = 0.25f),
    onTertiaryContainer = Color(0xFFEAF4F1),

    background = Color(0xFF0F1B18),
    onBackground = Color(0xFFEAF4F1),

    surface = Color(0xFF1A2E2A),
    onSurface = Color(0xFFEAF4F1),
    surfaceVariant = Color(0xFF152421),
    onSurfaceVariant = Color(0xFFB6C9C4),

    error = Color(0xFFEF6C6C),
    onError = Color(0xFF0F1B18),
    errorContainer = Color(0xFFEF6C6C).copy(alpha = 0.25f),
    onErrorContainer = Color(0xFFEF6C6C),

    outline = Color(0xFF35524D),
    outlineVariant = Color(0xFF2C4440),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFEAF4F1),
    inverseOnSurface = Color(0xFF0F1B18),
    inversePrimary = Color(0xFF6FD6A8),

    surfaceTint = Color(0xFF35B48A),
)

/**
 * Custom Material3 Theme composable
 * 
 * Usage:
 * ```
 * CooklyTheme {
 *     // Your composable content here
 *     YourScreen()
 * }
 * ```
 */
@Composable
fun CooklyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

