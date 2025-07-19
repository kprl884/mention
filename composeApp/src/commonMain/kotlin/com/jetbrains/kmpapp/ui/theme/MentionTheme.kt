package com.jetbrains.kmpapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Mention App Color Palette
private val LightColors = lightColorScheme(
    primary = Color(0xFF1DA1F2), // Twitter Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1F5FE),
    onPrimaryContainer = Color(0xFF01579B),
    secondary = Color(0xFF657786), // Twitter Gray
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F8FA),
    onSecondaryContainer = Color(0xFF14171A),
    tertiary = Color(0xFF17BF63), // Twitter Green
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5E8),
    onTertiaryContainer = Color(0xFF0D5C2E),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF14171A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14171A),
    surfaceVariant = Color(0xFFF5F8FA),
    onSurfaceVariant = Color(0xFF657786),
    outline = Color(0xFFE1E8ED),
    outlineVariant = Color(0xFFCCD6DD),
    error = Color(0xFFE0245E), // Twitter Red
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF1DA1F2), // Twitter Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF8899A6), // Twitter Gray
    onSecondary = Color(0xFF14171A),
    secondaryContainer = Color(0xFF192734),
    onSecondaryContainer = Color(0xFFF5F8FA),
    tertiary = Color(0xFF17BF63), // Twitter Green
    onTertiary = Color(0xFF14171A),
    tertiaryContainer = Color(0xFF0D5C2E),
    onTertiaryContainer = Color(0xFFE8F5E8),
    background = Color(0xFF14171A),
    onBackground = Color(0xFFF5F8FA),
    surface = Color(0xFF192734),
    onSurface = Color(0xFFF5F8FA),
    surfaceVariant = Color(0xFF253341),
    onSurfaceVariant = Color(0xFF8899A6),
    outline = Color(0xFF38444D),
    outlineVariant = Color(0xFF253341),
    error = Color(0xFFE0245E), // Twitter Red
    onError = Color(0xFF14171A),
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFEBEE)
)

@Composable
fun MentionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MentionTypography,
        content = content
    )
} 