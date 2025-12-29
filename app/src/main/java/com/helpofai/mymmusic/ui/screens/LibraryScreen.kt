package com.helpofai.mymmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.components.AnimatedItem
import com.helpofai.mymmusic.ui.components.AudioFileItem
import com.helpofai.mymmusic.ui.components.FlagshipTopBar
import com.helpofai.mymmusic.ui.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun LibraryScreen(
    viewModel: MusicViewModel,
    onBackClick: () -> Unit,
    onEqClick: () -> Unit
) {
    val audioFiles by viewModel.audioFiles.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    val fftData by viewModel.fftData.collectAsState()
    val leftLevel by viewModel.leftLevel.collectAsState()
    val rightLevel by viewModel.rightLevel.collectAsState()

    val topBarHeight = 64.dp
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
            contentPadding = PaddingValues(top = topBarHeight + 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.nav_library),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            itemsIndexed(audioFiles) { index, audioFile ->
                AnimatedItem(index = index) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                showBackButton = true,
                onNavigationClick = onBackClick,
                onSearchClick = {},
                onThemeClick = onEqClick
            )
        }
    }
}
