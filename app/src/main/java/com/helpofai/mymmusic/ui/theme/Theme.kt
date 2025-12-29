package com.helpofai.mymmusic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class ThemeType {
    GreenLight,
    PinkDark,
    Dynamic
}

// 🌿 Green Light Scheme
private val GreenLightScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenAccent,
    background = GreenBackground,
    surface = GreenSurface,
    surfaceVariant = GreenSurface,
    onPrimary = Color.White,
    onSecondary = Color(0xFF1B5E20),
    onBackground = Color(0xFF1B5E20),
    onSurface = Color(0xFF1B5E20),
    outline = GreenBorder
)

// 🌸 Pink Dark Scheme
private val PinkDarkScheme = darkColorScheme(
    primary = PinkPrimaryStart,
    secondary = PinkSecondary,
    tertiary = PinkAccent,
    background = PinkBackground,
    surface = PinkSurface,
    surfaceVariant = PinkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color(0xFFE0E0E0),
    outline = PinkBorder.copy(alpha = 0.5f)
)

@Composable
fun MyMMusicTheme(
    themeType: ThemeType = if (isSystemInDarkTheme()) ThemeType.PinkDark else ThemeType.GreenLight,
    dynamicExtractedColors: ExtractedColors? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.GreenLight -> GreenLightScheme
        ThemeType.PinkDark -> PinkDarkScheme
        ThemeType.Dynamic -> {
            if (dynamicExtractedColors != null) {
                darkColorScheme(
                    primary = Color(dynamicExtractedColors.primary),
                    secondary = Color(dynamicExtractedColors.secondary),
                    background = Color(dynamicExtractedColors.background),
                    surface = Color(dynamicExtractedColors.background).copy(alpha = 0.8f),
                    onPrimary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else PinkDarkScheme
        }
    }

    val gradients = when (themeType) {
        ThemeType.GreenLight -> GradientScheme(
            primaryGradient = Brush.linearGradient(listOf(GreenPrimary, GreenSecondary)),
            secondaryGradient = Brush.linearGradient(listOf(GreenSecondary, GreenSurface)),
            backgroundGradient = Brush.verticalGradient(listOf(GreenBackground, GreenSurface)),
            surfaceGradient = Brush.linearGradient(listOf(GreenSurface, Color.White))
        )
        ThemeType.PinkDark -> GradientScheme(
            primaryGradient = Brush.linearGradient(listOf(PinkPrimaryStart, PinkPrimaryEnd)),
            secondaryGradient = Brush.linearGradient(listOf(PinkSecondary, PinkAccent)),
            backgroundGradient = Brush.verticalGradient(listOf(PinkBackground, PinkSurface)),
            surfaceGradient = Brush.linearGradient(listOf(PinkSurface, PinkBackground))
        )
        ThemeType.Dynamic -> {
            if (dynamicExtractedColors != null) {
                val p = Color(dynamicExtractedColors.primary)
                val b = Color(dynamicExtractedColors.background)
                GradientScheme(
                    primaryGradient = Brush.linearGradient(listOf(p, Color(dynamicExtractedColors.secondary))),
                    secondaryGradient = Brush.linearGradient(listOf(p.copy(alpha = 0.5f), b)),
                    backgroundGradient = Brush.verticalGradient(listOf(b, b.copy(alpha = 0.8f))),
                    surfaceGradient = Brush.linearGradient(listOf(b, p.copy(alpha = 0.1f)))
                )
            } else {
                GradientScheme(
                    primaryGradient = Brush.linearGradient(listOf(PinkPrimaryStart, PinkPrimaryEnd)),
                    secondaryGradient = Brush.linearGradient(listOf(PinkSecondary, PinkAccent)),
                    backgroundGradient = Brush.verticalGradient(listOf(PinkBackground, PinkSurface)),
                    surfaceGradient = Brush.linearGradient(listOf(PinkSurface, PinkBackground))
                )
            }
        }
    }

    CompositionLocalProvider(LocalGradientScheme provides gradients) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}