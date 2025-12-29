package com.helpofai.mymmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 8.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .padding(0.dp) // Reset padding
    ) {
        content()
    }
}

@Composable
fun GlassySurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable BoxScope.() -> Unit
) {
    // Glassmorphism Simulation: High transparency + subtle gradient border + blur (if supported/simulated)
    // Note: True blur requires RenderEffect on Android 12+, here we simulate the "look"
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun PremiumPopup(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Popup(
        onDismissRequest = onDismissRequest,
        alignment = androidx.compose.ui.Alignment.Center
    ) {
        PremiumCard(
            modifier = Modifier.padding(32.dp),
            elevation = 16.dp,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                content()
            }
        }
    }
}
