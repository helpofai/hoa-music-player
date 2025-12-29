package com.helpofai.mymmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CircularVisualizer(
    fftData: ByteArray,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    tertiaryColor: Color = MaterialTheme.colorScheme.tertiary
) {
    // Process FFT Data
    val magnitudes = remember(fftData) {
        if (fftData.isEmpty()) FloatArray(0)
        else {
            val n = fftData.size / 2
            val m = FloatArray(n)
            for (i in 0 until n) {
                val r = fftData[2 * i].toInt()
                val im = fftData[2 * i + 1].toInt()
                m[i] = sqrt((r * r + im * im).toFloat())
            }
            m
        }
    }

    // Frequency Bins (Approximate)
    // 20Hz - 60Hz: Bass (Outer Glow)
    // 60Hz - 250Hz: Mids (Main Ring)
    // 250Hz - 20kHz: Highs (Inner Ring)
    
    val bassAvg = remember(magnitudes) {
        if (magnitudes.isEmpty()) 0f 
        else magnitudes.slice(0 until (magnitudes.size / 10).coerceAtLeast(1)).average().toFloat()
    }
    
    val midAvg = remember(magnitudes) {
        if (magnitudes.isEmpty()) 0f
        else magnitudes.slice((magnitudes.size / 10) until (magnitudes.size / 2)).average().toFloat()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val baseRadius = size.minDimension / 3
        
        // 1. Layer 3 - Outer Glow Ring (Bass)
        val bassPulse = (bassAvg / 100f).coerceIn(0f, 1f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(tertiaryColor.copy(alpha = 0.3f * bassPulse), Color.Transparent),
                center = center,
                radius = baseRadius * (1.2f + bassPulse * 0.5f)
            ),
            center = center,
            radius = baseRadius * (1.2f + bassPulse * 0.5f)
        )

        // 2. Layer 2 - Main Spectrum Ring (Mids)
        if (magnitudes.isNotEmpty()) {
            val barCount = 60
            val angleStep = (2 * PI / barCount).toFloat()
            
            for (i in 0 until barCount) {
                val magnitude = magnitudes[i % (magnitudes.size / 2)] / 50f
                val height = (baseRadius * 0.2f) + magnitude.coerceIn(0f, baseRadius * 0.4f)
                
                val angle = i * angleStep
                val startX = center.x + baseRadius * cos(angle)
                val startY = center.y + baseRadius * sin(angle)
                val endX = center.x + (baseRadius + height) * cos(angle)
                val endY = center.y + (baseRadius + height) * sin(angle)
                
                drawLine(
                    color = primaryColor.copy(alpha = 0.8f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }

        // 3. Layer 1 - Inner Micro Ring (Highs)
        val innerRadius = baseRadius * 0.8f
        if (magnitudes.size > 60) {
            val barCount = 120
            val angleStep = (2 * PI / barCount).toFloat()
            
            for (i in 0 until barCount) {
                val magnitude = magnitudes[(i + 60) % magnitudes.size] / 80f
                val height = magnitude.coerceIn(0f, baseRadius * 0.15f)
                
                val angle = i * angleStep
                val startX = center.x + innerRadius * cos(angle)
                val startY = center.y + innerRadius * sin(angle)
                val endX = center.x + (innerRadius - height) * cos(angle)
                val endY = center.y + (innerRadius - height) * sin(angle)
                
                drawLine(
                    color = secondaryColor.copy(alpha = 0.6f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
