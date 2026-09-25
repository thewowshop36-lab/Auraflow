package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AuraPrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = AuraPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = AuraCyanLight,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0E7490),
    onSecondaryContainer = Color.White,
    tertiary = AuraEmeraldLight,
    background = AuraDarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = AuraDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = AuraDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = AuraDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = AuraPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = AuraPrimaryDark,
    secondary = AuraCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF155E75),
    tertiary = AuraEmerald,
    background = AuraLightBackground,
    onBackground = Color(0xFF0F172A),
    surface = AuraLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = AuraLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = AuraLightOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to preserve distinctive AuraFlow brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
