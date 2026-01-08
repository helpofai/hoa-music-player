package com.helpofai.mymmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.data.model.Album
import com.helpofai.mymmusic.data.model.Artist
import com.helpofai.mymmusic.data.model.SmartPlaylist
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.components.FlagshipTopBar
import com.helpofai.mymmusic.ui.components.PremiumCard
import com.helpofai.mymmusic.ui.theme.AppTheme

@Composable
fun LibraryScreen(
    viewModel: MusicViewModel,
    onBackClick: () -> Unit,
    onEqClick: () -> Unit
) {
    val fftData by viewModel.fftData.collectAsState()
    val leftLevel by viewModel.leftLevel.collectAsState()
    val rightLevel by viewModel.rightLevel.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Playlists", "Artists", "Albums")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.gradients.backgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> PlaylistsTab(viewModel)
                    1 -> ArtistsTab(viewModel)
                    2 -> AlbumsTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun PlaylistsTab(viewModel: MusicViewModel) {
    val smartPlaylists by viewModel.smartPlaylists.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(smartPlaylists) { playlist ->
            SmartPlaylistRow(playlist) {
                if (playlist.tracks.isNotEmpty()) {
                    viewModel.playAudio(playlist.tracks, 0)
                }
            }
        }
    }
}

@Composable
fun ArtistsTab(viewModel: MusicViewModel) {
    val artists by viewModel.artists.collectAsState()
    val allFiles by viewModel.audioFiles.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(artists) { artist ->
            ArtistRow(artist) {
                // Play all songs by this artist
                val artistTracks = allFiles.filter { it.artist == artist.name }
                if (artistTracks.isNotEmpty()) {
                    viewModel.playAudio(artistTracks, 0)
                }
            }
        }
    }
}

@Composable
fun AlbumsTab(viewModel: MusicViewModel) {
    val albums by viewModel.albums.collectAsState()
    val allFiles by viewModel.audioFiles.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums) { album ->
            AlbumCard(album) {
                // Play all songs in this album
                val albumTracks = allFiles.filter { it.album == album.name }.sortedBy { it.title } // Should ideally sort by track number
                if (albumTracks.isNotEmpty()) {
                    viewModel.playAudio(albumTracks, 0)
                }
            }
        }
    }
}

@Composable
fun SmartPlaylistRow(playlist: SmartPlaylist, onClick: () -> Unit) {
    PremiumCard(
        modifier = Modifier.fillMaxWidth().height(80.dp).clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                playlist.icon?.let {
                    Icon(imageVector = it, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(playlist.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(playlist.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ArtistRow(artist: Artist, onClick: () -> Unit) {
    PremiumCard(
        modifier = Modifier.fillMaxWidth().height(72.dp).clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = artist.name.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(artist.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${artist.albumCount} Albums • ${artist.trackCount} Tracks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AlbumCard(album: Album, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(160.dp).clickable { onClick() }
    ) {
        PremiumCard(
            modifier = Modifier.size(160.dp),
            shape = MaterialTheme.shapes.large
        ) {
            if (album.artworkUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(album.artworkUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = album.artist,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
