package com.helpofai.mymmusic.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Folder
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.components.AnimatedItem
import com.helpofai.mymmusic.ui.components.FlagshipTopBar
import com.helpofai.mymmusic.ui.theme.AppTheme
import kotlin.math.roundToInt

@Composable
fun FoldersScreen(
    viewModel: MusicViewModel,
    onBackClick: () -> Unit,
    onFolderClick: (com.helpofai.mymmusic.data.model.MusicFolder) -> Unit,
    onEqClick: () -> Unit
) {
    val folders by viewModel.folders.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val fftData by viewModel.fftData.collectAsState()
    val leftLevel by viewModel.leftLevel.collectAsState()
    val rightLevel by viewModel.rightLevel.collectAsState()

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
            contentPadding = PaddingValues(top = topBarHeight + 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.nav_folders),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            itemsIndexed(folders) { index, folder ->
                AnimatedItem(index = index) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        FolderItem(folder = folder) {
                            onFolderClick(folder)
                        }
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

@Composable
fun FolderItem(folder: com.helpofai.mymmusic.data.model.MusicFolder, onClick: () -> Unit) {
    com.helpofai.mymmusic.ui.components.PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${folder.trackCount} tracks • ${folder.path}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
