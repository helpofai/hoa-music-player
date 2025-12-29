package com.helpofai.mymmusic.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.theme.AppTheme

@Composable
fun LyricsView(viewModel: MusicViewModel) {
    val currentLyrics by viewModel.currentLyrics.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val audioFiles by viewModel.audioFiles.collectAsState()
    val listState = rememberLazyListState()

    // Find current AudioFile for Art
    val currentAudioFile = remember(currentMediaItem, audioFiles) {
        audioFiles.find { it.id.toString() == currentMediaItem?.mediaId }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Blurry Background (Full Screen)
        val albumArtUri = currentAudioFile?.albumArtUri
        if (albumArtUri != null) {
            Image(
                painter = rememberAsyncImagePainter(albumArtUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f), // Dim it down
                contentScale = ContentScale.Crop
            )
            // Blur Mesh / Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                            )
                        )
                    )
            )
        } else {
            // Fallback Gradient if no art
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.gradients.backgroundGradient)
            )
        }

        if (currentLyrics == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No lyrics available", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return
        }

        val lines = currentLyrics!!.lines
        val currentTime = playbackState.currentPosition

        // Find active line index
        var activeIndex = -1
        for (i in lines.indices) {
            if (currentTime >= lines[i].timeMs) {
                activeIndex = i
            } else {
                break
            }
        }

        // Auto-scroll to active line
        LaunchedEffect(activeIndex) {
            if (activeIndex >= 0) {
                listState.animateScrollToItem(index = (activeIndex - 2).coerceAtLeast(0))
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 50.dp)
        ) {
            itemsIndexed(lines) { index, line ->
                val isActive = index == activeIndex
                
                // Animate scale and alpha for the active line
                val scale by animateFloatAsState(if (isActive) 1.1f else 0.95f, label = "scale")
                val alpha by animateFloatAsState(if (isActive) 1.0f else 0.4f, label = "alpha")
                val color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

                Text(
                    text = line.text,
                    style = MaterialTheme.typography.headlineMedium.copy( // Larger text for lyrics
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
                    ),
                    color = color,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp) // More spacing
                        .scale(scale)
                        .alpha(alpha)
                )
            }
        }
        
        // Top/Bottom Fade Gradients for smooth edges
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )
    }
}