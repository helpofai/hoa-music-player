package com.helpofai.mymmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.data.model.AudioFile
import com.helpofai.mymmusic.ui.theme.AppTheme

@Composable
fun FlagshipTopBar(
    isPlaying: Boolean = false,
    fftData: ByteArray = ByteArray(0),
    leftLevel: Float = 0f,
    rightLevel: Float = 0f,
    showBackButton: Boolean = false,
    onNavigationClick: () -> Unit,
    onSearchClick: () -> Unit,
    onThemeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassySurface(
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 0.dp)
                .fillMaxWidth()
                .statusBarsPadding()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = if (showBackButton) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                AdaptiveMusicIcon(
                    isPlaying = isPlaying,
                    fftData = fftData,
                    leftLevel = leftLevel,
                    rightLevel = rightLevel,
                    size = 36.dp,
                    iconSize = 20.dp
                )
            }

            Row {
                if (!showBackButton) {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
                IconButton(onClick = onThemeClick) {
                    Icon(
                        imageVector = Icons.Default.Equalizer, 
                        contentDescription = stringResource(R.string.mastering_console),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun HeroCard(
    currentItem: AudioFile?,
    isPlaying: Boolean,
    fftData: ByteArray = ByteArray(0),
    leftLevel: Float = 0f,
    rightLevel: Float = 0f,
    onPlayClick: () -> Unit,
    onClick: () -> Unit
) {
    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        elevation = 12.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.gradients.primaryGradient)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AudioBadge(text = "Hi-Res")
                        AudioBadge(text = "FLAC")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AdaptiveMusicIcon(
                        isPlaying = isPlaying,
                        fftData = fftData,
                        leftLevel = leftLevel,
                        rightLevel = rightLevel,
                        size = 80.dp,
                        iconSize = 40.dp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentItem?.title ?: stringResource(R.string.no_track_playing),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = currentItem?.artist ?: stringResource(R.string.select_a_song),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                    }

                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioBadge(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun QuickAccessGrid(
    onFoldersClick: () -> Unit = {},
    onLibraryClick: () -> Unit = {}
) {
    val items = listOf(
        Triple(stringResource(R.string.nav_songs), Icons.Default.MusicNote, onLibraryClick),
        Triple(stringResource(R.string.nav_albums), Icons.Default.Album, {}),
        Triple(stringResource(R.string.nav_folders), Icons.Default.Folder, onFoldersClick),
        Triple(stringResource(R.string.nav_artists), Icons.Default.Person, {}),
        Triple(stringResource(R.string.nav_library), Icons.Default.LibraryMusic, onLibraryClick),
        Triple(stringResource(R.string.nav_genres), Icons.Default.Mic, {})
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.browse_header),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        
        val chunkedItems = items.chunked(3)
        chunkedItems.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (label, icon, action) ->
                    QuickAccessTile(
                        label = label,
                        icon = icon,
                        modifier = Modifier.weight(1f),
                        onClick = action
                    )
                }
                if (rowItems.size < 3) {
                    Spacer(modifier = Modifier.weight((3 - rowItems.size).toFloat()))
                }
            }
        }
    }
}

@Composable
fun QuickAccessTile(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    PremiumCard(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.more),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
