package com.helpofai.mymmusic.ui

import androidx.lifecycle.ViewModel
import com.helpofai.mymmusic.ui.theme.ThemeType
import com.helpofai.mymmusic.ui.theme.ExtractedColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {
    private val _currentTheme = MutableStateFlow(ThemeType.PinkDark) 
    val currentTheme = _currentTheme.asStateFlow()

    private val _dynamicColors = MutableStateFlow<ExtractedColors?>(null)
    val dynamicColors = _dynamicColors.asStateFlow()

    fun setTheme(theme: ThemeType) {
        _currentTheme.value = theme
    }
    
    fun updateDynamicColors(colors: ExtractedColors?) {
        _dynamicColors.value = colors
        if (colors != null) {
            _currentTheme.value = ThemeType.Dynamic
        }
    }

    fun toggleTheme() {
        _currentTheme.value = when (_currentTheme.value) {
            ThemeType.PinkDark -> ThemeType.GreenLight
            ThemeType.GreenLight -> ThemeType.PinkDark
            ThemeType.Dynamic -> ThemeType.PinkDark
        }
    }
}