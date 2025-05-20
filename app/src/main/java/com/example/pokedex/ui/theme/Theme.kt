package com.example.pokedex.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Indigo = Color(0xFF4A4E69)
val ElectricBlue = Color(0xFF00B4D8)
val MidnightBlue = Color(0xFF22223B)
val GhostWhite = Color(0xFFF8F9FA)
val LightGray = Color(0xFFEDEDED)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo,
    secondary = ElectricBlue,
    background = MidnightBlue,
    surface = Color(0xFF232946),
    onPrimary = LightGray,
    onSecondary = LightGray,
    onBackground = LightGray,
    onSurface = LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    secondary = ElectricBlue,
    background = GhostWhite,
    surface = Color.White,
    onPrimary = MidnightBlue,
    onSecondary = MidnightBlue,
    onBackground = MidnightBlue,
    onSurface = MidnightBlue
)

@Composable
fun PokedexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default to use Pokemon theme
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
        typography = Typography,
        content = content
    )
}