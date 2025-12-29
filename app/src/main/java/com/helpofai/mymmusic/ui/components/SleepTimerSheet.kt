package com.helpofai.mymmusic.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.helpofai.mymmusic.R
import kotlin.math.roundToInt

@Composable
fun SleepTimerSheet(
    remainingMillis: Long?,
    onSetTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit
) {
    var customMinutes by remember { mutableFloatStateOf(30f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.sleep_timer),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (remainingMillis != null) {
            val minutes = (remainingMillis / 1000) / 60
            val seconds = (remainingMillis / 1000) % 60
            Text(
                text = stringResource(R.string.music_will_pause, minutes, seconds),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCancelTimer,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.TimerOff, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.cancel_timer))
            }
        } else {
            Text(
                text = stringResource(R.string.quick_presets),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            val presets = listOf(15, 30, 45, 60, 90)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets) { minutes ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { onSetTimer(minutes) }
                    ) {
                        Text(
                            text = "${minutes}m",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.custom_duration),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${customMinutes.roundToInt()} minutes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Slider(
                value = customMinutes,
                onValueChange = { customMinutes = it },
                valueRange = 1f..240f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSetTimer(customMinutes.roundToInt()) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Timer, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Start ${customMinutes.roundToInt()}m Timer")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
