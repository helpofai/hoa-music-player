package com.helpofai.mymmusic.media

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class EqBand(
    val id: Short,
    val centerFreq: Int, // in milliHertz
    val minLevel: Short,
    val maxLevel: Short,
    val currentLevel: Short
)

@Singleton
class AudioEffectManager @Inject constructor() {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    private val _eqBands = MutableStateFlow<List<EqBand>>(emptyList())
    val eqBands = _eqBands.asStateFlow()

    private val _bassStrength = MutableStateFlow<Short>(0)
    val bassStrength = _bassStrength.asStateFlow()

    private val _virtualizerStrength = MutableStateFlow<Short>(0)
    val virtualizerStrength = _virtualizerStrength.asStateFlow()

    private val _isEqEnabled = MutableStateFlow(false)
    val isEqEnabled = _isEqEnabled.asStateFlow()

    fun initialize(audioSessionId: Int) {
        release()
        try {
            // Equalizer
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
                _isEqEnabled.value = true
                loadBands(this)
            }

            // Bass Boost
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = true
                _bassStrength.value = roundedStrength
            }

            // Virtualizer
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = true
                _virtualizerStrength.value = roundedStrength
            }

        } catch (e: Exception) {
            Log.e("AudioEffectManager", "Error initializing effects", e)
        }
    }

    private fun loadBands(eq: Equalizer) {
        val bands = mutableListOf<EqBand>()
        val minLevel = eq.bandLevelRange[0]
        val maxLevel = eq.bandLevelRange[1]

        for (i in 0 until eq.numberOfBands) {
            bands.add(
                EqBand(
                    id = i.toShort(),
                    centerFreq = eq.getCenterFreq(i.toShort()),
                    minLevel = minLevel,
                    maxLevel = maxLevel,
                    currentLevel = eq.getBandLevel(i.toShort())
                )
            )
        }
        _eqBands.value = bands
    }

    fun setBandLevel(bandId: Short, level: Short) {
        equalizer?.let { eq ->
            try {
                eq.setBandLevel(bandId, level)
                // Refresh state
                loadBands(eq)
            } catch (e: Exception) {
                Log.e("AudioEffectManager", "Error setting band level", e)
            }
        }
    }

    fun setBassStrength(strength: Short) {
        bassBoost?.let { bb ->
            try {
                bb.setStrength(strength)
                _bassStrength.value = strength
            } catch (e: Exception) {
                Log.e("AudioEffectManager", "Error setting bass strength", e)
            }
        }
    }

    fun setVirtualizerStrength(strength: Short) {
        virtualizer?.let { virt ->
            try {
                virt.setStrength(strength)
                _virtualizerStrength.value = strength
            } catch (e: Exception) {
                Log.e("AudioEffectManager", "Error setting virtualizer strength", e)
            }
        }
    }
    
    fun setEqEnabled(enabled: Boolean) {
        equalizer?.enabled = enabled
        _isEqEnabled.value = enabled
    }

    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        
        equalizer = null
        bassBoost = null
        virtualizer = null
    }
}
