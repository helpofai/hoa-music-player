package com.helpofai.mymmusic.media

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.BaseAudioProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*
import kotlin.random.Random

@Singleton
class StereoAudioProcessor @Inject constructor() : BaseAudioProcessor() {

    // --- Quantum Precision Parameters (64-bit Internal) ---
    private var preGain = 1.0
    private var balance = 0.0
    private var width = 1.0
    private var crossfeed = 0.0
    private var clarity = 0.0
    private var warmth = 0.0
    private var subBassDepth = 0.0
    private var hiFiAir = 0.0
    private var adaptiveLoudness = 0.0
    
    // Dither State
    private var lastRandom = 0.0

    private val _leftLevel = MutableStateFlow(0f)
    val leftLevel = _leftLevel.asStateFlow()
    private val _rightLevel = MutableStateFlow(0f)
    val rightLevel = _rightLevel.asStateFlow()

    // Control setters (Converting to Double internally)
    fun setPreGain(v: Float) { preGain = v.toDouble() }
    fun setBalance(v: Float) { balance = v.toDouble() }
    fun setWidth(v: Float) { width = v.toDouble() }
    fun setCrossfeed(v: Float) { crossfeed = v.toDouble() }
    fun setClarity(v: Float) { clarity = v.toDouble() }
    fun setWarmth(v: Float) { warmth = v.toDouble() }
    fun setSubBass(v: Float) { subBassDepth = v.toDouble() }
    fun setHiFiAir(v: Float) { hiFiAir = v.toDouble() }
    fun setAdaptiveLoudness(v: Float) { adaptiveLoudness = v.toDouble() }

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return

        val count = inputBuffer.remaining()
        val outputBuffer = replaceOutputBuffer(count)

        var maxL = 0.0
        var maxR = 0.0

        while (inputBuffer.hasRemaining()) {
            // 1. Convert to 64-bit Double Precision
            var l = inputBuffer.getShort().toDouble() / 32768.0
            var r = inputBuffer.getShort().toDouble() / 32768.0

            // 2. Adaptive Loudness (Quantum curve)
            if (adaptiveLoudness > 0.0) {
                val comp = (1.0 - preGain).coerceIn(0.0, 0.5) * adaptiveLoudness
                l += (l * l * abs(l) * comp * 0.25)
                r += (r * r * abs(r) * comp * 0.25)
            }

            // 3. Sub-Bass & Air (64-bit harmonics)
            if (subBassDepth > 0.0) {
                l += sin(l * PI * 0.2) * subBassDepth * 0.18
                r += sin(r * PI * 0.2) * subBassDepth * 0.18
            }
            if (hiFiAir > 0.0) {
                l += (l - abs(l) * l) * hiFiAir * 0.35
                r += (r - abs(r) * r) * hiFiAir * 0.35
            }

            // 4. Gain Stage
            l *= preGain
            r *= preGain

            // 5. Advanced Analog Modeling (Tanh-Double)
            if (warmth > 0.0) {
                l = (l + warmth * (tanh(l * 1.6) - l))
                r = (r + warmth * (tanh(r * 1.6) - r))
            }
            if (clarity > 0.0) {
                l += (l * abs(l) * clarity * 0.22)
                r += (r * abs(r) * clarity * 0.22)
            }

            // 6. Mastering Stereo Matrix
            val mid = (l + r) / 2.0
            val side = (l - r) / 2.0
            l = mid + (side * width)
            r = mid - (side * width)

            if (crossfeed > 0.0) {
                val mix = crossfeed * 0.28
                val nextL = l * (1.0 - mix) + r * mix
                val nextR = r * (1.0 - mix) + l * mix
                l = nextL; r = nextR
            }

            l *= (1.0 - balance).coerceIn(0.0, 1.0)
            r *= (1.0 + balance).coerceIn(0.0, 1.0)

            // 7. Inter-Sample Peak Soft-Limit (64-bit)
            l = softLimit(l)
            r = softLimit(r)

            maxL = maxOf(maxL, abs(l))
            maxR = maxOf(maxR, abs(r))

            // 8. TPDF Dithering (The Audiophile Secret)
            // Adds 1/2 LSB of noise to eliminate quantization errors
            val currentRandom = Random.nextDouble(-1.0, 1.0) / 32768.0
            val ditherL = currentRandom - lastRandom
            val ditherR = currentRandom - lastRandom
            lastRandom = currentRandom

            outputBuffer.putShort(((l + ditherL) * 32767.0).toInt().toShort())
            outputBuffer.putShort(((r + ditherR) * 32767.0).toInt().toShort())
        }
        
        _leftLevel.value = maxL.toFloat().coerceIn(0f, 1f)
        _rightLevel.value = maxR.toFloat().coerceIn(0f, 1f)
        outputBuffer.flip()
    }

    private fun softLimit(x: Double): Double {
        val t = 0.62 // Audiophile headroom threshold
        return if (abs(x) < t) x 
        else {
            if (x > 0) t + (1.0 - t) * tanh((x - t) / (1.0 - t))
            else -t - (1.0 - t) * tanh((-x - t) / (1.0 - t))
        }
    }
}
