package com.example.unimatch.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF001E31),
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = Color(0xFF001D36),
    primary = Color(0xFF1976D2), // Blue
    onPrimary = Color.White,
    //primaryContainer = Color(0xFFBBDEFB),
    //onPrimaryContainer = Color(0xFF001E31),
    secondary = Color(0xFF2196F3),
    onSecondary = Color.White,
    //secondaryContainer = Color(0xFFE3F2FD),
    //onSecondaryContainer = Color(0xFF001D36),
    tertiary = Color(0xFF0D47A1),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD6E4FF),
    onTertiaryContainer = Color(0xFF001B3F),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF001849),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF001849)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9), // Light Blue
    onPrimary = Color(0xFF00325A),
    primaryContainer = Color(0xFF004881),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF00344C),
    secondaryContainer = Color(0xFF004B6F),
    onSecondaryContainer = Color(0xFFCDE5FF),
    tertiary = Color(0xFFACC7FF),
    onTertiary = Color(0xFF002F66),
    tertiaryContainer = Color(0xFF004494),
    onTertiaryContainer = Color(0xFFD6E4FF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF001849),
    onBackground = Color(0xFFDBE1FF),
    surface = Color(0xFF001849),
    onSurface = Color(0xFFDBE1FF)
)

@Composable
fun UnimatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = UniMatchTypography,
        content = content
    )
}