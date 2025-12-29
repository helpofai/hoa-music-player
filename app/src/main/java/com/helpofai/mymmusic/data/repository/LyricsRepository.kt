package com.helpofai.mymmusic.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.helpofai.mymmusic.data.model.AudioFile
import com.helpofai.mymmusic.data.model.LyricLine
import com.helpofai.mymmusic.data.model.Lyrics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Regex for LRC timestamp: [mm:ss.xx] or [mm:ss:xx]
    private val pattern = Pattern.compile("""\[(\d{2}):(\d{2})[.:](\d{2,3})\](.*)""")

    suspend fun getLyrics(audioFile: AudioFile): Lyrics? = withContext(Dispatchers.IO) {
        try {
            // 1. Try to find local .lrc file
            // Note: This requires the audioFile.uri to be a file path, which MediaStore URIs (content://) are not directly.
            // We need to resolve the real path or try to guess the location.
            // For modern Android (Scoped Storage), reading generic files next to media is hard without All Files Access.
            // However, we can try to resolve the absolute path if available.
            
            val path = getRealPathFromURI(audioFile.uri)
            if (path != null) {
                val audioFileObj = File(path)
                val parentDir = audioFileObj.parentFile
                val nameWithoutExt = audioFileObj.nameWithoutExtension
                val lrcFile = File(parentDir, "$nameWithoutExt.lrc")

                if (lrcFile.exists() && lrcFile.canRead()) {
                    return@withContext parseLrcFile(lrcFile)
                }
            }
            
            // 2. Fallback: Embedded lyrics (Would require MediaMetadataRetriever, skipped for now to keep it simple/safe) 
            
            // 3. Fallback: Mock lyrics for demo if none found
            return@withContext generateMockLyrics(audioFile)
            
        } catch (e: Exception) {
            Log.e("LyricsRepository", "Error loading lyrics", e)
            null
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(android.provider.MediaStore.Audio.Media.DATA)
        try {
            val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
            cursor?.moveToFirst()
            val path = columnIndex?.let { cursor.getString(it) }
            cursor?.close()
            return path
        } catch (e: Exception) {
            return null
        }
    }

    private fun parseLrcFile(file: File): Lyrics {
        val lines = mutableListOf<LyricLine>()
        file.forEachLine { line ->
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                val min = matcher.group(1)?.toLongOrNull() ?: 0
                val sec = matcher.group(2)?.toLongOrNull() ?: 0
                val msStr = matcher.group(3) ?: "00"
                // Normalize ms: if 2 digits, it's tens of ms (x10). If 3, it's straight ms.
                val ms = if (msStr.length == 2) msStr.toLong() * 10 else msStr.toLong()
                
                val text = matcher.group(4)?.trim() ?: ""
                
                val totalMs = (min * 60 * 1000) + (sec * 1000) + ms
                lines.add(LyricLine(totalMs, text))
            }
        }
        return Lyrics(lines.sortedBy { it.timeMs }, isSynced = true)
    }
    
    // Demo function to show UI capabilities even without real files
    private fun generateMockLyrics(audioFile: AudioFile): Lyrics {
        // Just generic placeholder text timed roughly
        val lines = listOf(
            LyricLine(1000, "Instrumental Intro..."),
            LyricLine(5000, "Listening to ${audioFile.title}"),
            LyricLine(10000, "By the amazing ${audioFile.artist}"),
            LyricLine(15000, "Imagine the lyrics appearing here"),
            LyricLine(20000, "Perfectly synced to the beat"),
            LyricLine(25000, "Add a .lrc file with the same name"),
            LyricLine(30000, "To see real lyrics in this spot"),
            LyricLine(35000, "(Guitar Solo)"),
            LyricLine(45000, "Music fades out...")
        )
        return Lyrics(lines, isSynced = true)
    }
}
