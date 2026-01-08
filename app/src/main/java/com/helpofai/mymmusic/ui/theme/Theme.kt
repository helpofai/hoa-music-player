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
    Rainbow,
    Dynamic
}

// 🌈 Rainbow Scheme
private val RainbowColors = listOf(
    Color(0xFFFF0000), // Red
    Color(0xFFFF7F00), // Orange
    Color(0xFFFFFF00), // Yellow
    Color(0xFF00FF00), // Green
    Color(0xFF0000FF), // Blue
    Color(0xFF4B0082), // Indigo
    Color(0xFF9400D3)  // Violet
)

private val RainbowScheme = darkColorScheme(
    primary = Color(0xFF00E5FF), // Cyan Accent
    secondary = Color(0xFF76FF03), // Green Accent
    tertiary = Color(0xFFFFEA00), // Yellow Accent
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color.White.copy(alpha = 0.3f)
)

// 🌿 Green Light Scheme
private val GreenLightScheme = lightColorScheme(
    primary = GreenPrimary,
// ... (GreenLightScheme content)
    outline = GreenBorder
)

// 🌸 Pink Dark Scheme
private val PinkDarkScheme = darkColorScheme(
    primary = PinkPrimaryStart,
// ... (PinkDarkScheme content)
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
        ThemeType.Rainbow -> RainbowScheme
        ThemeType.Dynamic -> {
            if (dynamicExtractedColors != null) {
                darkColorScheme(
                    primary = Color(dynamicExtractedColors.primary),
                    secondary = Color(dynamicExtractedColors.secondary),
                    tertiary = Color(dynamicExtractedColors.tertiary),
                    background = Color(dynamicExtractedColors.background),
                    surface = Color(dynamicExtractedColors.background).copy(alpha = 0.8f),
                    onPrimary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else RainbowScheme // Fallback to Rainbow for "Dynamic" feel if no song
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
        ThemeType.Rainbow -> GradientScheme(
            primaryGradient = Brush.linearGradient(RainbowColors),
            secondaryGradient = Brush.linearGradient(RainbowColors.reversed()),
            backgroundGradient = Brush.verticalGradient(
                colors = listOf(Color(0xFF121212)) + RainbowColors.map { it.copy(alpha = 0.15f) } + listOf(Color(0xFF121212))
            ),
            surfaceGradient = Brush.linearGradient(listOf(Color(0xFF1E1E1E), Color(0xFF252525)))
        )
        ThemeType.Dynamic -> {
            if (dynamicExtractedColors != null) {
                val p = Color(dynamicExtractedColors.primary)
                val s = Color(dynamicExtractedColors.secondary)
                val t = Color(dynamicExtractedColors.tertiary)
                val m = Color(dynamicExtractedColors.muted)
                val b = Color(dynamicExtractedColors.background)
                
                // Multi-Color Dynamic Gradients
                GradientScheme(
                    primaryGradient = Brush.linearGradient(listOf(p, s)),
                    secondaryGradient = Brush.linearGradient(listOf(s, t)),
                    // Complex Background: Muted (Top) -> Dark (Mid) -> Darkened Tertiary (Bottom Glow)
                    backgroundGradient = Brush.verticalGradient(
                        colors = listOf(
                            m.copy(alpha = 0.4f), 
                            b, 
                            b, 
                            t.copy(alpha = 0.3f)
                        )
                    ),
                    surfaceGradient = Brush.linearGradient(listOf(b.copy(alpha = 0.9f), b.copy(alpha = 0.95f)))
                )
            } else {
                // Rainbow Fallback
                GradientScheme(
                    primaryGradient = Brush.linearGradient(RainbowColors),
                    secondaryGradient = Brush.linearGradient(RainbowColors.reversed()),
                    backgroundGradient = Brush.verticalGradient(
                        colors = listOf(Color(0xFF121212)) + RainbowColors.map { it.copy(alpha = 0.1f) }
                    ),
                    surfaceGradient = Brush.linearGradient(listOf(Color(0xFF1E1E1E), Color(0xFF252525)))
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