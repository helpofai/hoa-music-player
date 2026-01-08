package com.helpofai.mymmusic.data.model

import android.net.Uri

data class Album(
    val name: String,
    val artist: String,
    val artworkUri: Uri?,
    val trackCount: Int,
    val year: Int
)

data class Artist(
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)
