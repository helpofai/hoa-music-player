package com.helpofai.mymmusic.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.data.model.AudioFile
import com.helpofai.mymmusic.data.model.SmartPlaylist
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.components.*
import com.helpofai.mymmusic.ui.theme.AppTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onThemeToggle: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    onSearchClick: () -> Unit,
    onFoldersClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onMenuClick: () -> Unit,
    onEqClick: () -> Unit
) {
    val audioFiles by viewModel.audioFiles.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val smartPlaylists by viewModel.smartPlaylists.collectAsState()
    val fftData by viewModel.fftData.collectAsState()
    val leftLevel by viewModel.leftLevel.collectAsState()
    val rightLevel by viewModel.rightLevel.collectAsState()
    val recentTracks by viewModel.recentTracks.collectAsState()
    
    val currentAudioFile = remember(currentMediaItem, audioFiles) {
        audioFiles.find { it.id.toString() == currentMediaItem?.mediaId }
    }

    val topBarHeight = 48.dp
    val topBarHeightPx = with(LocalDensity.current) { topBarHeight.roundToPx().toFloat() }
    var topBarOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = topBarOffsetHeightPx + delta
                topBarOffsetHeightPx = newOffset.coerceIn(-topBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.gradients.backgroundGradient)
            .nestedScroll(nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = topBarHeight + 16.dp, bottom = 120.dp),
        ) {
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    HeroCard(
                        currentItem = currentAudioFile,
                        isPlaying = isPlaying,
                        fftData = fftData,
                        leftLevel = leftLevel,
                        rightLevel = rightLevel,
                        onPlayClick = { viewModel.togglePlayPause() },
                        onClick = onNavigateToPlayer
                    )
                }
            }

            item {
                QuickAccessGrid(
                    onFoldersClick = onFoldersClick,
                    onLibraryClick = onLibraryClick
                )
            }

            if (recentTracks.isNotEmpty()) {
                item {
                    AnimatedItem(index = 1) {
                        Column {
                            SectionHeader(stringResource(R.string.recently_played))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(recentTracks) { index, file ->
                                    RecentlyPlayedItem(
                                        audioFile = file,
                                        isCurrentTrack = currentMediaItem?.mediaId == file.id.toString(),
                                        isPlaying = isPlaying,
                                        fftData = fftData,
                                        leftLevel = leftLevel,
                                        rightLevel = rightLevel,
                                        onClick = { viewModel.playAudio(recentTracks, index) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (smartPlaylists.isNotEmpty()) {
                item {
                    AnimatedItem(index = 2) {
                        Column {
                            SectionHeader(stringResource(R.string.for_you))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(smartPlaylists) { _, playlist ->
                                    SmartPlaylistItem(playlist) {
                                        if (playlist.tracks.isNotEmpty()) {
                                            viewModel.playAudio(playlist.tracks, 0)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                AnimatedItem(index = 3) {
                    SectionHeader(stringResource(R.string.all_songs))
                }
            }

            itemsIndexed(audioFiles) { index, audioFile ->
                AnimatedItem(index = index + 4) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        AudioFileItem(
                            audioFile = audioFile,
                            isCurrentTrack = currentMediaItem?.mediaId == audioFile.id.toString(),
                            isPlaying = isPlaying,
                            fftData = fftData,
                            leftLevel = leftLevel,
                            rightLevel = rightLevel,
                            onClick = { viewModel.playAudio(audioFiles, index) }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = topBarOffsetHeightPx.roundToInt()) }
                .align(Alignment.TopCenter)
        ) {
            FlagshipTopBar(
                isPlaying = isPlaying,
                fftData = fftData,
                leftLevel = leftLevel,
                rightLevel = rightLevel,
                onNavigationClick = onMenuClick,
                onSearchClick = onSearchClick,
                onThemeClick = onEqClick
            )
        }
        
        // MiniPlayer Removed from here
    }
}

@Composable
fun RecentlyPlayedItem(
    audioFile: AudioFile,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    fftData: ByteArray,
    leftLevel: Float,
    rightLevel: Float,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        PremiumCard(
            modifier = Modifier.size(120.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AdaptiveMusicIcon(
                    isPlaying = isCurrentTrack && isPlaying,
                    fftData = fftData,
                    leftLevel = leftLevel,
                    rightLevel = rightLevel,
                    size = 120.dp,
                    iconSize = 48.dp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = audioFile.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = audioFile.artist,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SmartPlaylistItem(playlist: SmartPlaylist, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        PremiumCard(
            modifier = Modifier.size(160.dp),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                playlist.icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = playlist.description,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}