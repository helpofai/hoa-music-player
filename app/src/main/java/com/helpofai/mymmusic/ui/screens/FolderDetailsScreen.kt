package com.helpofai.mymmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.components.AnimatedItem
import com.helpofai.mymmusic.ui.components.AudioFileItem
import com.helpofai.mymmusic.ui.theme.AppTheme
import java.io.File

@Composable
fun FolderDetailsScreen(
    viewModel: MusicViewModel,
    folderPath: String,
    onBackClick: () -> Unit
) {
    val decodedPath = remember(folderPath) { java.net.URLDecoder.decode(folderPath, "UTF-8") }
    val folderName = remember(decodedPath) { decodedPath.substringAfterLast(File.separator) }
    val tracks = remember(decodedPath) { viewModel.getTracksInFolder(decodedPath) }
    
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val fftData by viewModel.fftData.collectAsState()
    val leftLevel by viewModel.leftLevel.collectAsState()
    val rightLevel by viewModel.rightLevel.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.gradients.backgroundGradient)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Text(
                        text = folderName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Button(
                    onClick = { if (tracks.isNotEmpty()) viewModel.playAudio(tracks, 0) },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Play All")
                }
            }

            // Track List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(tracks) { index, audioFile ->
                    AnimatedItem(index = index) {
                        AudioFileItem(
                            audioFile = audioFile,
                            isCurrentTrack = currentMediaItem?.mediaId == audioFile.id.toString(),
                            isPlaying = isPlaying,
                            fftData = fftData,
                            leftLevel = leftLevel,
                            rightLevel = rightLevel,
                            onClick = { viewModel.playAudio(tracks, index) }
                        )
                    }
                }
            }
        }
    }
}
