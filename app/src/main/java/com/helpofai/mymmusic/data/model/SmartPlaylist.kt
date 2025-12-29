package com.helpofai.mymmusic.data.model

data class SmartPlaylist(
    val id: String,
    val title: String,
    val description: String,
    val tracks: List<AudioFile>,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
)
