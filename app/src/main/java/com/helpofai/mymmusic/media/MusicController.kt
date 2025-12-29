package com.helpofai.mymmusic.media

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.helpofai.mymmusic.data.model.AudioFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PlaybackState(
    val currentPosition: Long,
    val duration: Long
)

@Singleton
class MusicController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    val currentMediaItem = _currentMediaItem.asStateFlow()

    private val _currentPlaylist = MutableStateFlow<List<MediaItem>>(emptyList())
    val currentPlaylist = _currentPlaylist.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState(0L, 0L))
    val playbackState = _playbackState.asStateFlow()

    // Sleep Timer State
    private val _sleepTimerMillis = MutableStateFlow<Long?>(null)
    val sleepTimerMillis = _sleepTimerMillis.asStateFlow()
    private var sleepTimerJob: Job? = null

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                mediaController?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        if (isPlaying) startProgressUpdate() else stopProgressUpdate()
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        _currentMediaItem.value = mediaItem
                        updatePlaylist()
                        updateProgress()
                    }

                    override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
                        updatePlaylist()
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_READY) {
                            updateProgress()
                            updatePlaylist()
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun updatePlaylist() {
        mediaController?.let { controller ->
            val items = mutableListOf<MediaItem>()
            for (i in 0 until controller.mediaItemCount) {
                items.add(controller.getMediaItemAt(i))
            }
            _currentPlaylist.value = items
        }
    }

    fun seekToItem(index: Int) {
        mediaController?.seekTo(index, 0L)
        mediaController?.play()
    }

    private fun startProgressUpdate() {
        stopProgressUpdate()
        progressJob = scope.launch {
            while (isActive) {
                updateProgress()
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updateProgress() {
        mediaController?.let { player ->
            _playbackState.value = PlaybackState(
                currentPosition = player.currentPosition,
                duration = player.duration.coerceAtLeast(0L)
            )
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun skipToNext() {
        mediaController?.seekToNext()
    }

    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    fun playAudio(tracks: List<AudioFile>, startIndex: Int) {
        val mediaItems = tracks.map { audioFile ->
            MediaItem.Builder()
                .setMediaId(audioFile.id.toString())
                .setUri(audioFile.uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(audioFile.title)
                        .setArtist(audioFile.artist)
                        .setAlbumTitle(audioFile.album)
                        .setArtworkUri(audioFile.albumArtUri)
                        .build()
                )
                .build()
        }

        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    // Sleep Timer Methods
    fun setSleepTimer(minutes: Int) {
        cancelSleepTimer()
        if (minutes <= 0) return
        
        val totalMillis = minutes * 60 * 1000L
        _sleepTimerMillis.value = totalMillis
        
        sleepTimerJob = scope.launch {
            var remaining = totalMillis
            while (remaining > 0) {
                delay(1000)
                remaining -= 1000
                _sleepTimerMillis.value = remaining
            }
            mediaController?.pause()
            _sleepTimerMillis.value = null
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerMillis.value = null
    }

    fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        scope.cancel()
    }
}
