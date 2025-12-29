package com.helpofai.mymmusic.data.model

import android.net.Uri

data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val albumArtUri: Uri? = null,
    val dateAdded: Long = 0,
    val year: Int = 0,
    val path: String = ""
)
