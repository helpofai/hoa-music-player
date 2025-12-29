package com.helpofai.mymmusic.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ExtractedColors(
    val primary: Int,
    val secondary: Int,
    val background: Int
)

object ColorExtractor {
    suspend fun extractFromUri(context: Context, uri: Uri?): ExtractedColors? = withContext(Dispatchers.IO) {
        if (uri == null) return@withContext null
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                val palette = Palette.from(bitmap).generate()
                val primary = palette.getVibrantColor(0xFFBB86FC.toInt())
                val secondary = palette.getLightVibrantColor(0xFF03DAC6.toInt())
                val dark = palette.getDarkMutedColor(0xFF121212.toInt())
                
                return@withContext ExtractedColors(primary, secondary, dark)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        null
    }
}
