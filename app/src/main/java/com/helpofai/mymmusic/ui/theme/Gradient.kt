package com.helpofai.mymmusic.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class GradientScheme(
    val primaryGradient: Brush,
    val secondaryGradient: Brush,
    val backgroundGradient: Brush,
    val surfaceGradient: Brush
)

val LocalGradientScheme = staticCompositionLocalOf {
    GradientScheme(
        primaryGradient = Brush.linearGradient(listOf(Color.Unspecified, Color.Unspecified)),
        secondaryGradient = Brush.linearGradient(listOf(Color.Unspecified, Color.Unspecified)),
        backgroundGradient = Brush.linearGradient(listOf(Color.Unspecified, Color.Unspecified)),
        surfaceGradient = Brush.linearGradient(listOf(Color.Unspecified, Color.Unspecified))
    )
}

// Custom hook to access gradients easily
object AppTheme {
    val gradients: GradientScheme
        @Composable
        get() = LocalGradientScheme.current
}
