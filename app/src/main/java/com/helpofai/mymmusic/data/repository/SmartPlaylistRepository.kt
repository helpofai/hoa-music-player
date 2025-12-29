package com.helpofai.mymmusic.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.WbSunny
import com.helpofai.mymmusic.data.model.AudioFile
import com.helpofai.mymmusic.data.model.SmartPlaylist
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartPlaylistRepository @Inject constructor() {

    fun generateSmartPlaylists(allTracks: List<AudioFile>): List<SmartPlaylist> {
        val playlists = mutableListOf<SmartPlaylist>()
        if (allTracks.isEmpty()) return emptyList()

        // 1. Recently Added
        val recentlyAdded = allTracks.sortedByDescending { it.dateAdded }.take(15)
        playlists.add(
            SmartPlaylist(
                id = "recent",
                title = "New Arrivals",
                description = "Freshly added to your library",
                tracks = recentlyAdded,
                icon = Icons.Default.NewReleases
            )
        )

        // 2. Time-Based Playlist
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timePlaylist = when (hour) {
            in 5..11 -> SmartPlaylist(
                id = "morning",
                title = "Morning Melodies",
                description = "Start your day with these",
                tracks = allTracks.shuffled().take(20),
                icon = Icons.Default.WbSunny
            )
            in 12..17 -> SmartPlaylist(
                id = "afternoon",
                title = "Afternoon Energy",
                description = "Keep going through the day",
                tracks = allTracks.shuffled().take(20),
                icon = Icons.Default.WbSunny
            )
            else -> SmartPlaylist(
                id = "night",
                title = "Late Night Vibes",
                description = "Smooth sounds for the night",
                tracks = allTracks.shuffled().take(20),
                icon = Icons.Default.Bedtime
            )
        }
        playlists.add(timePlaylist)

        // 3. Throwbacks (Old tracks based on year)
        val throwbacks = allTracks.filter { it.year > 0 && it.year < 2015 }.take(15)
        if (throwbacks.isNotEmpty()) {
            playlists.add(
                SmartPlaylist(
                    id = "throwback",
                    title = "Throwback Classics",
                    description = "Memories from the past",
                    tracks = throwbacks,
                    icon = Icons.Default.History
                )
            )
        }

        return playlists
    }
}
