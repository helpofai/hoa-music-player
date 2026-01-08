package com.helpofai.mymmusic.media

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.audio.BaseAudioProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
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
    
    // 8D Audio State
    private var is8DEnabled = false
    private var rotationSpeed = 0.12 // Hz
    private var currentRotationPhase = 0.0
    private var sampleRate = 48000
    private var filterL = 0.0
    private var filterR = 0.0
    
    // --- Audiophile Crossover (Linkwitz-Riley 4th Order) ---
    // 24dB/octave separation for perfect isolation of Bass vs Air
    private class Biquad {
        var a0 = 1.0; var a1 = 0.0; var a2 = 0.0
        var b0 = 1.0; var b1 = 0.0; var b2 = 0.0
        var z1 = 0.0; var z2 = 0.0

        fun process(input: Double): Double {
            val out = input * b0 + z1
            z1 = input * b1 + z2 - out * a1
            z2 = input * b2 - out * a2
            return out
        }
    }

    private val lp1L = Biquad(); private val lp2L = Biquad() // Cascaded for LPF
    private val hp1L = Biquad(); private val hp2L = Biquad() // Cascaded for HPF
    private val lp1R = Biquad(); private val lp2R = Biquad()
    private val hp1R = Biquad(); private val hp2R = Biquad()

    // DC Blocker State (Removes sub-audible offset)
    private var dcX1L = 0.0; var dcY1L = 0.0
    private var dcX1R = 0.0; var dcY1R = 0.0
    
    // Bass Limiter State
    private var bassDriveL = 0.0
    private var bassDriveR = 0.0
    
    // Transient Shaper State
    private var envL = 0.0
    private var envR = 0.0
    
    // Dither State
    private var lastRandom = 0.0

    private val _leftLevel = MutableStateFlow(0f)
    val leftLevel = _leftLevel.asStateFlow()
    private val _rightLevel = MutableStateFlow(0f)
    val rightLevel = _rightLevel.asStateFlow()

    private lateinit var currentFormat: AudioFormat
    private var outputEncoding = C.ENCODING_PCM_16BIT

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
    
    fun set8DMode(enabled: Boolean) { is8DEnabled = enabled }
    fun set8DSpeed(speed: Float) { rotationSpeed = speed.toDouble() }

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        val encoding = inputAudioFormat.encoding
        if (encoding != C.ENCODING_PCM_16BIT && 
            encoding != C.ENCODING_PCM_FLOAT &&
            encoding != C.ENCODING_PCM_24BIT &&
            encoding != C.ENCODING_PCM_32BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
        currentFormat = inputAudioFormat
        sampleRate = inputAudioFormat.sampleRate
        calculateCrossover(150.0) // 150Hz Crossover Point
        
        // Intelligent Output Format Selection
        outputEncoding = if (encoding == C.ENCODING_PCM_16BIT) {
            C.ENCODING_PCM_16BIT // Keep 16-bit for standard audio
        } else {
            C.ENCODING_PCM_FLOAT // Upgrade everything else to 32-bit Float
        }
        
        // Force Stereo Output (Channel Count 2)
        return AudioFormat(inputAudioFormat.sampleRate, 2, outputEncoding)
    }
    
    private fun calculateCrossover(fc: Double) {
        val w0 = 2.0 * PI * fc / sampleRate
        val cosW0 = cos(w0)
        val alpha = sin(w0) / (2.0 * 0.7071) // Q = 0.7071

        // LPF Coefficients
        val lpfA0 = 1.0 + alpha
        val lpfB0 = (1.0 - cosW0) / 2.0
        val lpfB1 = 1.0 - cosW0
        val lpfB2 = (1.0 - cosW0) / 2.0
        val lpfA1 = -2.0 * cosW0
        val lpfA2 = 1.0 - alpha
        
        // HPF Coefficients
        val hpfA0 = 1.0 + alpha
        val hpfB0 = (1.0 + cosW0) / 2.0
        val hpfB1 = -(1.0 + cosW0)
        val hpfB2 = (1.0 + cosW0) / 2.0
        val hpfA1 = -2.0 * cosW0
        val hpfA2 = 1.0 - alpha

        // Apply to objects (normalized by a0)
        fun update(bq: Biquad, b0: Double, b1: Double, b2: Double, a0: Double, a1: Double, a2: Double) {
            bq.b0 = b0 / a0; bq.b1 = b1 / a0; bq.b2 = b2 / a0
            bq.a0 = 1.0;     bq.a1 = a1 / a0; bq.a2 = a2 / a0
        }
        
        listOf(lp1L, lp2L, lp1R, lp2R).forEach { update(it, lpfB0, lpfB1, lpfB2, lpfA0, lpfA1, lpfA2) }
        listOf(hp1L, hp2L, hp1R, hp2R).forEach { update(it, hpfB0, hpfB1, hpfB2, hpfA0, hpfA1, hpfA2) }
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return
        
        // Ensure native byte order for correct reading
        inputBuffer.order(ByteOrder.nativeOrder())

        val inputCount = inputBuffer.remaining()
        // If Mono -> Stereo, output size is double the input size. 
        // Also account for size difference if converting Int -> Float (byte size might change)
        // 16-bit (2 bytes) -> 16-bit (2 bytes) : Ratio 1
        // 16-bit (2 bytes) -> Float (4 bytes) : Ratio 2 (Not happening per logic above)
        // 24-bit (3 bytes) -> Float (4 bytes) : Ratio 1.33
        // 32-bit (4 bytes) -> Float (4 bytes) : Ratio 1
        
        // Simplest safe approach: Calculate sample count, then multiply by output frame size
        val bytesPerInputSample = when(currentFormat.encoding) {
            C.ENCODING_PCM_16BIT -> 2
            C.ENCODING_PCM_24BIT -> 3
            else -> 4
        }
        val numInputSamples = inputCount / bytesPerInputSample
        
        // If input is mono, we have 1 sample per frame. If stereo, 2.
        // We always output stereo (2 channels).
        val numFrames = numInputSamples / currentFormat.channelCount
        val numOutputSamples = numFrames * 2 // Always Stereo output
        
        val bytesPerOutputSample = if (outputEncoding == C.ENCODING_PCM_FLOAT) 4 else 2
        val outputSizeBytes = numOutputSamples * bytesPerOutputSample
        
        val outputBuffer = replaceOutputBuffer(outputSizeBytes)
        
        // 8D Audio Constants
        val phaseIncrement = if (is8DEnabled) (2.0 * PI * rotationSpeed) / sampleRate else 0.0
        
        // Dither (Only needed for 16-bit output)
        val applyDither = outputEncoding == C.ENCODING_PCM_16BIT
        
        var maxL = 0.0
        var maxR = 0.0

        while (inputBuffer.hasRemaining()) {
            // --- READ ---
            var l: Double
            var r: Double
            
            when (currentFormat.encoding) {
                C.ENCODING_PCM_16BIT -> {
                    l = inputBuffer.getShort().toDouble() / 32768.0
                    r = if (currentFormat.channelCount == 1) l else inputBuffer.getShort().toDouble() / 32768.0
                }
                C.ENCODING_PCM_FLOAT -> {
                    l = inputBuffer.getFloat().toDouble()
                    r = if (currentFormat.channelCount == 1) l else inputBuffer.getFloat().toDouble()
                }
                C.ENCODING_PCM_32BIT -> {
                    l = inputBuffer.getInt().toDouble() / 2147483648.0
                    r = if (currentFormat.channelCount == 1) l else inputBuffer.getInt().toDouble() / 2147483648.0
                }
                C.ENCODING_PCM_24BIT -> {
                    fun read24(): Double {
                        val b1 = inputBuffer.get().toInt() and 0xFF
                        val b2 = inputBuffer.get().toInt() and 0xFF
                        val b3 = inputBuffer.get().toInt() // Signed
                        return ((b3 shl 16) or (b2 shl 8) or b1).toDouble() / 8388608.0
                    }
                    l = read24()
                    r = if (currentFormat.channelCount == 1) l else read24()
                }
                else -> { l = 0.0; r = 0.0 } // Should not happen
            }

            // --- PROCESS (64-bit Core) ---
            
            // 2. Adaptive Loudness (Quantum curve)
            if (adaptiveLoudness > 0.0) {
                val comp = (1.0 - preGain).coerceIn(0.0, 0.5) * adaptiveLoudness
                l += (l * l * abs(l) * comp * 0.25)
                r += (r * r * abs(r) * comp * 0.25)
            }

            // 3. Hi-Fi Air (64-bit harmonics)
            if (hiFiAir > 0.0) {
                l += (l - abs(l) * l) * hiFiAir * 0.35
                r += (r - abs(r) * r) * hiFiAir * 0.35
            }

            // 4. Gain Stage
            l *= preGain
            r *= preGain

            // 5. Tape Saturation (Warmer than Tanh)
            if (warmth > 0.0) {
                val drive = 1.0 + warmth
                l = (l * drive) / (1.0 + abs(l * drive * 0.5))
                r = (r * drive) / (1.0 + abs(r * drive * 0.5))
            }

            // --- SPLIT BANDS (Linkwitz-Riley 4th Order) ---
            val lowL = lp2L.process(lp1L.process(l))
            val lowR = lp2R.process(lp1R.process(r))
            
            var highL = hp2L.process(hp1L.process(l))
            var highR = hp2R.process(hp1R.process(r))

            // Apply Clarity (Transient Shaper + Exciter) to Highs
            if (clarity > 0.0) {
                // 1. Harmonic Exciter (Shimmer)
                val harmonicL = highL * abs(highL) * 0.2
                val harmonicR = highR * abs(highR) * 0.2
                
                // 2. Transient Shaper (Punch)
                envL = envL * 0.9 + abs(highL) * 0.1
                envR = envR * 0.9 + abs(highR) * 0.1
                
                val transL = abs(highL) - envL
                val transR = abs(highR) - envR
                
                val boostL = if (transL > 0) transL * 0.6 else 0.0
                val boostR = if (transR > 0) transR * 0.6 else 0.0
                
                highL += (harmonicL + boostL) * clarity
                highR += (harmonicR + boostR) * clarity
            }

            // 6. Mastering Stereo Matrix
            
            // Process Highs (Widening with Vocal Protection)
            var midHigh = (highL + highR) * 0.5
            val sideHigh = (highL - highR) * 0.5
            
            // Vocal Protection: If widening, boost Mid slightly to keep vocals focus
            if (width > 1.0) {
                midHigh *= (1.0 + (width - 1.0) * 0.15) 
            }
            
            // Apply width to Side Highs
            highL = midHigh + (sideHigh * width)
            highR = midHigh - (sideHigh * width)
            
            val midLow = (lowL + lowR) * 0.5
            val sideLow = (lowL - lowR) * 0.5
            val bassWidth = if (width > 1.0) 0.0 else width
            var finalLowL = midLow + (sideLow * bassWidth)
            var finalLowR = midLow - (sideLow * bassWidth)

            // --- ADVANCED BASS ENHANCEMENT ---
            if (subBassDepth > 0.0) {
                val drive = 1.0 + subBassDepth * 0.5
                finalLowL = harmonicExcite(finalLowL, drive)
                finalLowR = harmonicExcite(finalLowR, drive)
                
                val threshold = 0.85
                if (abs(finalLowL) > threshold) finalLowL = (threshold + (1.0 - threshold) * tanh((finalLowL - threshold) / (1.0 - threshold)))
                if (abs(finalLowR) > threshold) finalLowR = (threshold + (1.0 - threshold) * tanh((finalLowR - threshold) / (1.0 - threshold)))
            }

            // Recombine
            l = highL + finalLowL
            r = highR + finalLowR

            if (crossfeed > 0.0) {
                val mix = crossfeed * 0.28
                val nextL = l * (1.0 - mix) + r * mix
                val nextR = r * (1.0 - mix) + l * mix
                l = nextL; r = nextR
            }

            l *= (1.0 - balance).coerceIn(0.0, 1.0)
            r *= (1.0 + balance).coerceIn(0.0, 1.0)
            
            // 7. 8D Audio Logic
            if (is8DEnabled) {
                currentRotationPhase += phaseIncrement
                if (currentRotationPhase > 2.0 * PI) currentRotationPhase -= 2.0 * PI
                
                val pan = sin(currentRotationPhase)
                val depth = cos(currentRotationPhase)
                val angle = (pan + 1.0) * PI / 4.0
                var gainL = cos(angle)
                var gainR = sin(angle)
                
                val distanceMix = (1.0 - depth) / 2.0
                val volumeScalar = 1.0 - (distanceMix * 0.3)
                val lpfAlpha = 1.0 - (distanceMix * 0.85) 
                
                filterL += lpfAlpha * (l - filterL)
                filterR += lpfAlpha * (r - filterR)
                
                l = filterL * gainL * volumeScalar
                r = filterR * gainR * volumeScalar
            }
            
            // 8. DC Blocker
            val tempL = l - dcX1L + 0.995 * dcY1L
            dcX1L = l; dcY1L = tempL; l = tempL
            val tempR = r - dcX1R + 0.995 * dcY1R
            dcX1R = r; dcY1R = tempR; r = tempR

            // 9. Inter-Sample Peak Soft-Limit
            l = softLimit(l)
            r = softLimit(r)

            maxL = maxOf(maxL, abs(l))
            maxR = maxOf(maxR, abs(r))

            // 10. TPDF Dithering (Only for 16-bit output)
            if (applyDither) {
                val currentRandom = Random.nextDouble(-1.0, 1.0) / 32768.0
                val ditherL = currentRandom - lastRandom
                val ditherR = currentRandom - lastRandom
                lastRandom = currentRandom
                l += ditherL
                r += ditherR
            }
            
            // --- WRITE ---
            if (outputEncoding == C.ENCODING_PCM_FLOAT) {
                outputBuffer.putFloat(l.toFloat())
                outputBuffer.putFloat(r.toFloat())
            } else {
                outputBuffer.putShort((l * 32767.0).toInt().toShort())
                outputBuffer.putShort((r * 32767.0).toInt().toShort())
            }
        }
        
        _leftLevel.value = maxL.toFloat().coerceIn(0f, 1f)
        _rightLevel.value = maxR.toFloat().coerceIn(0f, 1f)
        outputBuffer.flip()
    }
    
    private fun harmonicExcite(x: Double, drive: Double): Double {
        val s = x * drive
        // Soft clip curve: 1.5*x - 0.5*x^3
        val limited = if (abs(s) > 1.0) (if (s > 0) 1.0 else -1.0) else (1.5 * s - 0.5 * s * s * s)
        return x + (limited - x) * subBassDepth * 0.45
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
