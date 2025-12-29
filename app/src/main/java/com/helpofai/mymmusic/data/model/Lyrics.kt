package com.helpofai.mymmusic.data.model

data class LyricLine(
    val timeMs: Long,
    val text: String
)

data class Lyrics(
    val lines: List<LyricLine>,
    val isSynced: Boolean = true
)
