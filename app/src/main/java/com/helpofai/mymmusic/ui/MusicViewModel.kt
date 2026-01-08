package com.helpofai.mymmusic.ui

import android.media.AudioDeviceInfo
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpofai.mymmusic.data.local.HistoryDao
import com.helpofai.mymmusic.data.local.RecentTrack
import com.helpofai.mymmusic.data.model.*
import com.helpofai.mymmusic.data.repository.*
import com.helpofai.mymmusic.media.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: AudioRepository,
    private val musicController: MusicController,
    private val audioEffectManager: AudioEffectManager,
    private val smartPlaylistRepository: SmartPlaylistRepository,
    private val lyricsRepository: LyricsRepository,
    private val audioOutputRepository: AudioOutputRepository,
    private val visualizerManager: VisualizerManager,
    private val stereoAudioProcessor: StereoAudioProcessor,
    private val historyDao: HistoryDao
) : ViewModel() {

    private val _audioFiles = MutableStateFlow<List<AudioFile>>(emptyList())
    val audioFiles = _audioFiles.asStateFlow()
    
    // Real Recently Played Logic
    val recentTracks = historyDao.getRecentTrackIds()
        .combine(_audioFiles) { history, files ->
            history.mapNotNull { recent ->
                files.find { it.id == recent.trackId }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val folders = _audioFiles.map { files ->
        files.groupBy { 
            val file = File(it.path)
            file.parent ?: "Root"
        }.map { (path, folderFiles) ->
            MusicFolder(
                name = path.substringAfterLast(File.separator),
                path = path,
                trackCount = folderFiles.size
            )
        }.sortedBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lyrics State
    private val _currentLyrics = MutableStateFlow<Lyrics?>(null)
    val currentLyrics = _currentLyrics.asStateFlow()

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults = combine(_searchQuery, _audioFiles) { query, files ->
        if (query.isBlank()) {
            emptyList<AudioFile>()
        } else {
            files.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val smartPlaylists = _audioFiles.map { 
        smartPlaylistRepository.generateSmartPlaylists(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isPlaying = musicController.isPlaying
    val currentMediaItem = musicController.currentMediaItem
    val currentPlaylist = musicController.currentPlaylist
    val playbackState = musicController.playbackState
    
    // Visualizer State
    val fftData = visualizerManager.fftData
    
    // Stereo Visualizer Levels
    val leftLevel = stereoAudioProcessor.leftLevel
    val rightLevel = stereoAudioProcessor.rightLevel

    // EQ & DSP State
    val eqBands = audioEffectManager.eqBands
    val bassStrength = audioEffectManager.bassStrength
    val virtualizerStrength = audioEffectManager.virtualizerStrength
    val isEqEnabled = audioEffectManager.isEqEnabled

    // Professional Stereo/DSP State
    private val _stereoWidth = MutableStateFlow(1.0f)
    val stereoWidth = _stereoWidth.asStateFlow()
    private val _stereoBalance = MutableStateFlow(0f)
    val stereoBalance = _stereoBalance.asStateFlow()
    private val _preAmp = MutableStateFlow(1.0f)
    val preAmp = _preAmp.asStateFlow()

    private val _crossfeed = MutableStateFlow(0f)
    val crossfeed = _crossfeed.asStateFlow()

    private val _clarity = MutableStateFlow(0f)
    val clarity = _clarity.asStateFlow()

    private val _warmth = MutableStateFlow(0f)
    val warmth = _warmth.asStateFlow()

    private val _subBass = MutableStateFlow(0f)
    val subBass = _subBass.asStateFlow()

    private val _hiFiAir = MutableStateFlow(0f)
    val hiFiAir = _hiFiAir.asStateFlow()

    private val _adaptiveLoudness = MutableStateFlow(0f)
    val adaptiveLoudness = _adaptiveLoudness.asStateFlow()

    // 8D Audio State
    private val _is8DEnabled = MutableStateFlow(false)
    val is8DEnabled = _is8DEnabled.asStateFlow()
    
    private val _rotationSpeed = MutableStateFlow(0.12f)
    val rotationSpeed = _rotationSpeed.asStateFlow()

    // Audio Output State
    val availableOutputs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        audioOutputRepository.getAvailableDevices()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        MutableStateFlow(emptyList())
    }
    val selectedOutput = audioOutputRepository.selectedDevice
    val sleepTimerMillis = musicController.sleepTimerMillis

    init {
        loadAudioFiles()
        
        // Listen for track changes to load lyrics
        viewModelScope.launch {
            musicController.currentMediaItem.collect { mediaItem ->
                if (mediaItem != null) {
                    val audioId = mediaItem.mediaId.toLongOrNull()
                    val audioFile = _audioFiles.value.find { it.id == audioId }
                    if (audioFile != null) {
                        _currentLyrics.value = lyricsRepository.getLyrics(audioFile)
                    } else {
                         _currentLyrics.value = null
                    }
                }
            }
        }
    }

    private fun loadAudioFiles() {
        viewModelScope.launch {
            _audioFiles.value = repository.getLocalAudioFiles()
        }
    }

    fun getTracksInFolder(path: String): List<AudioFile> {
        return _audioFiles.value.filter { 
            val file = File(it.path)
            file.parent == path
        }
    }

    fun playAudio(tracks: List<AudioFile>, startIndex: Int) {
        val currentTrack = tracks.getOrNull(startIndex)
        currentTrack?.let {
            viewModelScope.launch {
                historyDao.insertOrUpdate(RecentTrack(it.id, System.currentTimeMillis()))
            }
        }
        musicController.playAudio(tracks, startIndex)
    }

    fun togglePlayPause() {
        musicController.togglePlayPause()
    }
    
    fun seekTo(position: Long) {
        musicController.seekTo(position)
    }
    
    fun skipToNext() {
        musicController.skipToNext()
    }
    
    fun skipToPrevious() {
        musicController.skipToPrevious()
    }

    fun seekToItem(index: Int) {
        musicController.seekToItem(index)
    }
    
    // EQ Methods
    fun setEqBandLevel(bandId: Short, level: Short) {
        audioEffectManager.setBandLevel(bandId, level)
    }
    
    fun setBassStrength(strength: Short) {
        audioEffectManager.setBassStrength(strength)
    }
    
    fun setVirtualizerStrength(strength: Short) {
        audioEffectManager.setVirtualizerStrength(strength)
    }
    
    fun setEqEnabled(enabled: Boolean) {
        audioEffectManager.setEqEnabled(enabled)
    }
    
    fun setStereoWidth(width: Float) {
        _stereoWidth.value = width
        stereoAudioProcessor.setWidth(width)
    }

    fun setStereoBalance(balance: Float) {
        _stereoBalance.value = balance
        stereoAudioProcessor.setBalance(balance)
    }

    fun setPreAmp(value: Float) {
        _preAmp.value = value
        stereoAudioProcessor.setPreGain(value)
    }

    fun setCrossfeed(value: Float) {
        _crossfeed.value = value
        stereoAudioProcessor.setCrossfeed(value)
    }

    fun setClarity(value: Float) {
        _clarity.value = value
        stereoAudioProcessor.setClarity(value)
    }

    fun setWarmth(value: Float) {
        _warmth.value = value
        stereoAudioProcessor.setWarmth(value)
    }

    fun setSubBass(value: Float) {
        _subBass.value = value
        stereoAudioProcessor.setSubBass(value)
    }

    fun setHiFiAir(value: Float) {
        _hiFiAir.value = value
        stereoAudioProcessor.setHiFiAir(value)
    }

    fun setAdaptiveLoudness(value: Float) {
        _adaptiveLoudness.value = value
        stereoAudioProcessor.setAdaptiveLoudness(value)
    }
    
    fun set8DMode(enabled: Boolean) {
        _is8DEnabled.value = enabled
        stereoAudioProcessor.set8DMode(enabled)
    }

    fun set8DSpeed(speed: Float) {
        _rotationSpeed.value = speed
        stereoAudioProcessor.set8DSpeed(speed)
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectAudioOutput(device: AudioDeviceInfo) {
        audioOutputRepository.selectDevice(device)
    }
    
    fun clearAudioOutput() {
        audioOutputRepository.clearSelection()
    }

    fun setSleepTimer(minutes: Int) {
        musicController.setSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        musicController.cancelSleepTimer()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
