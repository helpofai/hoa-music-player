package com.helpofai.mymmusic.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.components.*
import com.helpofai.mymmusic.ui.theme.AppTheme

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    viewModel: MusicViewModel,
    onBackClick: () -> Unit,
    onEqClick: () -> Unit
) {
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val availableOutputs by viewModel.availableOutputs.collectAsState()
    val selectedOutput by viewModel.selectedOutput.collectAsState()
    val fftData by viewModel.fftData.collectAsState()
    val leftLevel by viewModel.leftLevel.collectAsState()
    val rightLevel by viewModel.rightLevel.collectAsState()
    
    var showLyrics by remember { mutableStateOf(false) }
    var showAudioOutput by remember { mutableStateOf(false) }
    var showPlaylist by remember { mutableStateOf(false) }
    
    val lyricsSheetState = rememberModalBottomSheetState()
    val audioOutputSheetState = rememberModalBottomSheetState()
    val playlistSheetState = rememberModalBottomSheetState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.gradients.backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    text = stringResource(R.string.now_playing),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 2.sp
                )
                IconButton(onClick = { /* TODO: Menu */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            ) {
                if (isPlaying) {
                    ParticleVisualizer(
                        fftData = fftData,
                        leftLevel = leftLevel,
                        rightLevel = rightLevel,
                        modifier = Modifier.fillMaxSize(),
                        primaryColor = MaterialTheme.colorScheme.primary,
                        secondaryColor = MaterialTheme.colorScheme.secondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(120.dp)
                        .alpha(if (isPlaying) 0.1f else 0.5f),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentMediaItem?.mediaMetadata?.title?.toString() ?: stringResource(R.string.unknown_track),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentMediaItem?.mediaMetadata?.artist?.toString() ?: stringResource(R.string.unknown_artist),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = { /* Favorite */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AudioBadge("Hi-Res")
                    AudioBadge("FLAC")
                    AudioBadge("24-bit")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                var sliderPosition by remember { mutableStateOf(0f) }
                var isDragging by remember { mutableStateOf(false) }
                
                val currentPos = if (isDragging) sliderPosition.toLong() else playbackState.currentPosition
                val duration = playbackState.duration.coerceAtLeast(1L)

                Slider(
                    value = if (isDragging) sliderPosition else currentPos.toFloat(),
                    onValueChange = { 
                        isDragging = true
                        sliderPosition = it
                    },
                    onValueChangeFinished = {
                        viewModel.seekTo(sliderPosition.toLong())
                        isDragging = false
                    },
                    valueRange = 0f..duration.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPos),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.skipToPrevious() }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurface)
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(12.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                ) {
                    IconButton(onClick = { viewModel.togglePlayPause() }, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                IconButton(onClick = { viewModel.skipToNext() }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            GlassySurface(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ToolButton(icon = Icons.Default.Equalizer, label = stringResource(R.string.nav_equalizer), onClick = onEqClick)
                    ToolButton(icon = Icons.Default.QueueMusic, label = stringResource(R.string.queue), onClick = { showPlaylist = true })
                    ToolButton(
                        icon = Icons.Default.SettingsInputComponent, 
                        label = stringResource(R.string.output), 
                        onClick = { showAudioOutput = true }
                    )
                    ToolButton(icon = Icons.Default.Lyrics, label = stringResource(R.string.lyrics), onClick = { showLyrics = true })
                }
            }
        }
        
        if (showLyrics) {
            ModalBottomSheet(
                onDismissRequest = { showLyrics = false },
                sheetState = lyricsSheetState,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxSize()
            ) {
                LyricsView(viewModel = viewModel)
            }
        }

        if (showAudioOutput) {
            ModalBottomSheet(
                onDismissRequest = { showAudioOutput = false },
                sheetState = audioOutputSheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                AudioOutputPicker(
                    devices = availableOutputs,
                    selectedDevice = selectedOutput,
                    onDeviceSelected = { 
                        viewModel.selectAudioOutput(it)
                        showAudioOutput = false
                    },
                    onSystemDefaultSelected = {
                        viewModel.clearAudioOutput()
                        showAudioOutput = false
                    }
                )
            }
        }

        if (showPlaylist) {
            ModalBottomSheet(
                onDismissRequest = { showPlaylist = false },
                sheetState = playlistSheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                PlaylistSheet(
                    playlist = currentPlaylist,
                    currentMediaItem = currentMediaItem,
                    onTrackClick = { index ->
                        viewModel.seekToItem(index)
                        showPlaylist = false
                    }
                )
            }
        }
    }
}

@Composable
fun ToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
