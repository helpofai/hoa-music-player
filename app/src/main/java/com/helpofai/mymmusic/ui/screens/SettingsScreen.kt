package com.helpofai.mymmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.ui.ThemeViewModel
import com.helpofai.mymmusic.ui.theme.AppTheme
import com.helpofai.mymmusic.ui.theme.ThemeType

@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel,
    onBackClick: () -> Unit
) {
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.gradients.backgroundGradient)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    text = stringResource(R.string.nav_settings),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Theme Selection
            SettingsHeader("Appearance")
            ThemeOption(
                title = "Pink Dark Mode (OLED)",
                isSelected = currentTheme == ThemeType.PinkDark,
                onClick = { themeViewModel.setTheme(ThemeType.PinkDark) }
            )
            ThemeOption(
                title = "Green Light Mode",
                isSelected = currentTheme == ThemeType.GreenLight,
                onClick = { themeViewModel.setTheme(ThemeType.GreenLight) }
            )
            ThemeOption(
                title = "Dynamic (Album Based)",
                isSelected = currentTheme == ThemeType.Dynamic,
                onClick = { themeViewModel.setTheme(ThemeType.Dynamic) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Audio Quality Info
            SettingsHeader("Audio Engine")
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HighQuality, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.dsp_32bit), fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Processing is currently at 64-bit precision for highest fidelity.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun ThemeOption(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = MaterialTheme.colorScheme.onSurface)
        RadioButton(selected = isSelected, onClick = onClick)
    }
}