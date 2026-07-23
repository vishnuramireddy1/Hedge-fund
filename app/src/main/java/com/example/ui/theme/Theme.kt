package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = SapphireBlue,
    onPrimary = TextContrast,
    primaryContainer = SapphireBlueGlow,
    onPrimaryContainer = SapphireBlue,
    secondary = BullishGreen,
    onSecondary = TextContrast,
    secondaryContainer = BullishGreenGlow,
    onSecondaryContainer = BullishGreen,
    tertiary = WarningAmber,
    onTertiary = TextContrast,
    error = BearishRed,
    onError = TextContrast,
    errorContainer = BearishRedGlow,
    onErrorContainer = BearishRed,
    background = TerminalBackground,
    onBackground = TextPrimary,
    surface = TerminalCard,
    onSurface = TextPrimary,
    surfaceVariant = TerminalCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = TerminalBorder,
    outlineVariant = TerminalBorderLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Dark Terminal theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

