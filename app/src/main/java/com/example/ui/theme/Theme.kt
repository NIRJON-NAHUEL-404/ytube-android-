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

private val DarkColorScheme =
  darkColorScheme(
    primary = TubeRed,
    onPrimary = Color.White,
    primaryContainer = TubeDarkRed,
    onPrimaryContainer = Color.White,
    secondary = DataSaverGreen,
    onSecondary = Color.Black,
    tertiary = AccentYellow,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMutedDark,
    outline = DarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TubeRed,
    onPrimary = Color.White,
    primaryContainer = TubeRedLight,
    onPrimaryContainer = Color.White,
    secondary = DataSaverGreen,
    onSecondary = Color.Black,
    tertiary = AccentYellow,
    background = LightBackground,
    onBackground = Color(0xFF0F0F0F),
    surface = LightSurface,
    onSurface = Color(0xFF0F0F0F),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextMutedLight,
    outline = LightOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek modern dark theme for YouTube experience
  dynamicColor: Boolean = false, // Keep consistent YouTube brand styling
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
