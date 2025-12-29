package com.helpofai.mymmusic.media

import android.media.AudioManager
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.helpofai.mymmusic.data.repository.AudioOutputRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink

@AndroidEntryPoint
class MediaPlaybackService : MediaSessionService() {

    @Inject
    lateinit var audioEffectManager: AudioEffectManager
    
    @Inject
    lateinit var audioOutputRepository: AudioOutputRepository

    @Inject
    lateinit var visualizerManager: VisualizerManager

    @Inject
    lateinit var stereoAudioProcessor: StereoAudioProcessor

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val renderersFactory = object : DefaultRenderersFactory(this) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean
            ): AudioSink {
                return DefaultAudioSink.Builder(context)
                    .setAudioProcessors(arrayOf(stereoAudioProcessor))
                    .build()
            }
        }

        player = ExoPlayer.Builder(this, renderersFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        mediaSession = MediaSession.Builder(this, player).build()
        
        // Observe output device changes
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            serviceScope.launch {
                audioOutputRepository.selectedDevice.collect { device ->
                    if (device != null) {
                        audioManager.setCommunicationDevice(device)
                    } else {
                        audioManager.clearCommunicationDevice()
                    }
                }
            }
        }
        
        // Initialize effects and visualizer
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                 if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                     audioEffectManager.initialize(audioSessionId)
                     visualizerManager.initialize(audioSessionId)
                 }
            }
        })
        
        if (player.audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
            audioEffectManager.initialize(player.audioSessionId)
            visualizerManager.initialize(player.audioSessionId)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        audioEffectManager.release()
        visualizerManager.release()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}