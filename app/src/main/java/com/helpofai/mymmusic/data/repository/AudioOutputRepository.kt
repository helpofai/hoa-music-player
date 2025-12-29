package com.helpofai.mymmusic.data.repository

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioOutputRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _selectedDevice = MutableStateFlow<AudioDeviceInfo?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.M)
    fun getAvailableDevices(): Flow<List<AudioDeviceInfo>> = callbackFlow {
        val callback = object : android.media.AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                trySend(getOutputDevices())
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                trySend(getOutputDevices())
            }
        }

        audioManager.registerAudioDeviceCallback(callback, null)
        trySend(getOutputDevices()) // Initial emission

        awaitClose {
            audioManager.unregisterAudioDeviceCallback(callback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOutputDevices(): List<AudioDeviceInfo> {
        return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList()
    }

    fun selectDevice(device: AudioDeviceInfo) {
        _selectedDevice.value = device
    }
    
    fun clearSelection() {
        _selectedDevice.value = null
    }
}
