package com.helpofai.mymmusic.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedItem(
    index: Int = 0,
    delayPerItem: Int = 50,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val translationY = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        delay((index * delayPerItem).toLong().coerceAtMost(500)) // Cap delay so deep items don't wait forever
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
        }
        launch {
            translationY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translationY.value
        }
    ) {
        content()
    }
}
