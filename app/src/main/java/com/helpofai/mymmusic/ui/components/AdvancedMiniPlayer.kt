package com.helpofai.mymmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpofai.mymmusic.ui.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun AdvancedMiniPlayer(
    title: String,
    artist: String,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    fftData: ByteArray,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                },
                onDragStopped = {
                    if (offsetX > 300) onSkipPrevious()
                    else if (offsetX < -300) onSkipNext()
                    offsetX = 0f
                }
            )
            .shadow(20.dp, RoundedCornerShape(20.dp), spotColor = MaterialTheme.colorScheme.primary)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        GlassySurface(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // 1. Precise Gradient Progress Bar (Top)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(AppTheme.gradients.primaryGradient)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 2. Adaptive Visualizer Icon
                    AdaptiveMusicIcon(
                        isPlaying = isPlaying,
                        fftData = fftData,
                        size = 44.dp,
                        iconSize = 24.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // 3. Track Info + Timer
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = artist,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(
                                text = " • ${formatTime(currentPosition)} / ${formatTime(duration)}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )
                        }
                    }

                    // 4. Compact Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onSkipPrevious) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(24.dp))
                        }
                        
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = onTogglePlayPause) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        IconButton(onClick = onSkipNext) {
                            Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}