package com.helpofai.mymmusic.media

import android.media.audiofx.Visualizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisualizerManager @Inject constructor() {
    private var visualizer: Visualizer? = null
    
    private val _fftData = MutableStateFlow(ByteArray(0))
    val fftData = _fftData.asStateFlow()

    fun initialize(audioSessionId: Int) {
        release()
        try {
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(v: Visualizer?, waveform: ByteArray?, samplingRate: Int) {}

                    override fun onFftDataCapture(v: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                        if (fft != null) {
                            _fftData.value = fft.copyOf()
                        }
                    }
                }, Visualizer.getMaxCaptureRate(), false, true)
                enabled = true
            }
        } catch (e: Exception) {
            Log.e("VisualizerManager", "Error initializing visualizer", e)
        }
    }

    fun release() {
        visualizer?.enabled = false
        visualizer?.release()
        visualizer = null
    }
}
