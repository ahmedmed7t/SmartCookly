package org.nexable.period_pergnancy_tracker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nexable.smartcookly.theme.CooklyLightColors
import com.nexable.smartcookly.theme.CooklyDarkColors

/**
 * Custom Material3 ColorScheme for Light Theme
 */
private val LightColorScheme = lightColorScheme(
    primary = CooklyLightColors.Primary,
    onPrimary = CooklyLightColors.TextOnPrimary,
    primaryContainer = CooklyLightColors.PrimaryVariant,
    onPrimaryContainer = CooklyLightColors.TextOnPrimary,
    
    secondary = CooklyLightColors.Lavender,
    onSecondary = CooklyLightColors.TextOnPrimary,
    secondaryContainer = CooklyLightColors.Lavender.copy(alpha = 0.2f),
    onSecondaryContainer = CooklyLightColors.Plum,
    
    tertiary = CooklyLightColors.Plum,
    onTertiary = CooklyLightColors.TextOnPrimary,
    tertiaryContainer = CooklyLightColors.Plum.copy(alpha = 0.2f),
    onTertiaryContainer = CooklyLightColors.TextPrimary,
    
    background = CooklyLightColors.Background,
    onBackground = CooklyLightColors.TextPrimary,
    
    surface = CooklyLightColors.Surface,
    onSurface = CooklyLightColors.TextPrimary,
    surfaceVariant = CooklyLightColors.BackgroundSecondary,
    onSurfaceVariant = CooklyLightColors.TextSecondary,
    
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    outline = CooklyLightColors.Border,
    outlineVariant = CooklyLightColors.Divider,
    
    scrim = Color(0xFF000000),
    inverseSurface = CooklyLightColors.Plum,
    inverseOnSurface = CooklyLightColors.TextOnPrimary,
    inversePrimary = CooklyLightColors.PrimaryVariant,
    
    surfaceTint = CooklyLightColors.Primary,
)

/**
 * Custom Material3 ColorScheme for Dark Theme
 */
private val DarkColorScheme = darkColorScheme(
    primary = CooklyDarkColors.Primary,
    onPrimary = CooklyDarkColors.TextPrimary,
    primaryContainer = CooklyDarkColors.Primary.copy(alpha = 0.3f),
    onPrimaryContainer = CooklyDarkColors.TextPrimary,
    
    secondary = CooklyDarkColors.Lavender,
    onSecondary = CooklyDarkColors.TextPrimary,
    secondaryContainer = CooklyDarkColors.Lavender.copy(alpha = 0.2f),
    onSecondaryContainer = CooklyDarkColors.TextPrimary,
    
    tertiary = CooklyDarkColors.Plum,
    onTertiary = CooklyDarkColors.TextPrimary,
    tertiaryContainer = CooklyDarkColors.Plum.copy(alpha = 0.2f),
    onTertiaryContainer = CooklyDarkColors.TextPrimary,
    
    background = CooklyDarkColors.Background,
    onBackground = CooklyDarkColors.TextPrimary,
    
    surface = CooklyDarkColors.Surface,
    onSurface = CooklyDarkColors.TextPrimary,
    surfaceVariant = CooklyDarkColors.BackgroundSecondary,
    onSurfaceVariant = CooklyDarkColors.TextSecondary,
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = CooklyDarkColors.Divider,
    outlineVariant = CooklyDarkColors.Divider.copy(alpha = 0.5f),
    
    scrim = Color(0xFF000000),
    inverseSurface = CooklyDarkColors.TextPrimary,
    inverseOnSurface = CooklyDarkColors.Plum,
    inversePrimary = CooklyDarkColors.Primary,
    
    surfaceTint = CooklyDarkColors.Primary,
)

/**
 * Custom theme object that provides access to custom colors
 */
object CooklyTheme {
    /**
     * Get the current custom colors based on dark theme
     */
    @Composable
    fun colors(isDark: Boolean = isSystemInDarkTheme()): CooklyThemeColors {
        return if (isDark) {
            CooklyThemeColors.Dark
        } else {
            CooklyThemeColors.Light
        }
    }
}

/**
 * Sealed class to hold theme-specific custom colors
 */
sealed class CooklyThemeColors {
    abstract val background: Color
    abstract val backgroundSecondary: Color
    abstract val surface: Color
    abstract val surfaceElevated: Color
    abstract val primary: Color
    abstract val primaryVariant: Color
    abstract val lavender: Color
    abstract val plum: Color
    abstract val textPrimary: Color
    abstract val textSecondary: Color
    abstract val textMuted: Color
    abstract val textOnPrimary: Color
    abstract val textDisabled: Color
    abstract val divider: Color
    abstract val border: Color
    abstract val disabledBackground: Color
    
    object Light : CooklyThemeColors() {
        override val background = CooklyLightColors.Background
        override val backgroundSecondary = CooklyLightColors.BackgroundSecondary
        override val surface = CooklyLightColors.Surface
        override val surfaceElevated = CooklyLightColors.SurfaceElevated
        override val primary = CooklyLightColors.Primary
        override val primaryVariant = CooklyLightColors.PrimaryVariant
        override val lavender = CooklyLightColors.Lavender
        override val plum = CooklyLightColors.Plum
        override val textPrimary = CooklyLightColors.TextPrimary
        override val textSecondary = CooklyLightColors.TextSecondary
        override val textMuted = CooklyLightColors.TextMuted
        override val textOnPrimary = CooklyLightColors.TextOnPrimary
        override val textDisabled = CooklyLightColors.TextDisabled
        override val divider = CooklyLightColors.Divider
        override val border = CooklyLightColors.Border
        override val disabledBackground = CooklyLightColors.DisabledBackground
    }
    
    object Dark : CooklyThemeColors() {
        override val background = CooklyDarkColors.Background
        override val backgroundSecondary = CooklyDarkColors.BackgroundSecondary
        override val surface = CooklyDarkColors.Surface
        override val surfaceElevated = CooklyDarkColors.SurfaceElevated
        override val primary = CooklyDarkColors.Primary
        override val primaryVariant = CooklyDarkColors.PrimaryVariant
        override val lavender = CooklyDarkColors.Lavender
        override val plum = CooklyDarkColors.Plum
        override val textPrimary = CooklyDarkColors.TextPrimary
        override val textSecondary = CooklyDarkColors.TextSecondary
        override val textMuted = CooklyDarkColors.TextMuted
        override val textOnPrimary = CooklyDarkColors.TextOnPrimary
        override val textDisabled = CooklyDarkColors.TextDisabled
        override val divider = CooklyDarkColors.Divider
        override val border = CooklyDarkColors.Border
        override val disabledBackground = CooklyDarkColors.DisabledBackground
    }
}

/**
 * Custom Material3 Theme composable
 * 
 * Usage:
 * ```
 * PPTheme {
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

/**
 * Extension function to get custom theme colors from MaterialTheme
 */
@Composable
fun MaterialTheme.cooklyColors(): CooklyThemeColors {
    return CooklyTheme.colors()
}
