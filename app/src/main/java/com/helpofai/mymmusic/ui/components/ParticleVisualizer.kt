package com.helpofai.mymmusic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.withFrameMillis
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private data class Particle(
    var angle: Float,
    var speed: Float,
    var radius: Float,
    var alpha: Float,
    val color: Color,
    val offset: Float
)

@Composable

fun ParticleVisualizer(

    fftData: ByteArray,

    leftLevel: Float = 0f,

    rightLevel: Float = 0f,

    modifier: Modifier = Modifier,

    primaryColor: Color = MaterialTheme.colorScheme.primary,

    secondaryColor: Color = MaterialTheme.colorScheme.secondary

) {

    // Audio processing

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



    val bassEnergy = remember(magnitudes) {

        if (magnitudes.isEmpty()) 0f

        else {

            // Focus on the lowest bins for bass (0-5)

            val bassBins = magnitudes.take(8)

            bassBins.average().toFloat()

        }

    }



    // Particle System

    val particles = remember {

        List(100) { index ->

            Particle(

                angle = Random.nextFloat() * 2 * Math.PI.toFloat(),

                speed = 0.1f + Random.nextFloat() * 0.4f,

                radius = 3f + Random.nextFloat() * 5f,

                alpha = 0.4f + Random.nextFloat() * 0.6f,

                color = if (Random.nextBoolean()) primaryColor else secondaryColor,

                offset = Random.nextFloat() * 100f

            )

        }

    }



    // Animation Loop

    var time by remember { mutableFloatStateOf(0f) }

    

    LaunchedEffect(Unit) {

        while (this.isActive) {

            withFrameMillis { 

                time += 0.015f 

            }

        }

    }



    Canvas(modifier = modifier.fillMaxSize()) {

        val centerX = size.width / 2

        val centerY = size.height / 2

        val maxRadius = size.minDimension / 1.5f



        val energyFactor = (bassEnergy / 30f).coerceIn(0f, 2f) 



        particles.forEachIndexed { index, p ->

            // Stereo Mapping: index 0-49 Left, 50-99 Right

            val isLeft = index < particles.size / 2

            val channelFactor = if (isLeft) leftLevel else rightLevel

            

            // Adjust speed and distance based on specific channel hit

            val currentSpeed = p.speed * (1f + (energyFactor + channelFactor) * 2f)

            val progress = ((time * currentSpeed + p.offset) % 100f) / 100f

            

            // Constrain angle based on channel (Left side vs Right side)

            // Left: PI/2 to 3PI/2, Right: -PI/2 to PI/2

            val sideAngle = if (isLeft) {

                (PI / 2 + (p.angle % PI)).toFloat()

            } else {

                (-PI / 2 + (p.angle % PI)).toFloat()

            }

            

            val distance = progress * maxRadius

            val px = centerX + cos(sideAngle) * distance

            val py = centerY + sin(sideAngle) * distance

            

            val distanceAlpha = if (progress < 0.1f) progress * 10f else (1f - progress)

            val visualRadius = p.radius * (1f + channelFactor)

            

            drawCircle(

                color = p.color.copy(alpha = (p.alpha * distanceAlpha).coerceIn(0f, 1f)),

                radius = visualRadius,

                center = Offset(px, py)

            )

        }

        

        // Center "Heartbeat" Glow (Bass)

        drawCircle(

            color = primaryColor.copy(alpha = (0.3f * energyFactor).coerceIn(0f, 0.6f)),

            radius = (size.minDimension / 4) * (1f + energyFactor * 0.2f),

            center = Offset(centerX, centerY)

        )

    }

}



    