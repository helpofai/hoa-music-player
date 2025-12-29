package com.helpofai.mymmusic.ui.components

import android.media.AudioDeviceInfo
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AudioOutputPicker(
    devices: List<AudioDeviceInfo>,
    selectedDevice: AudioDeviceInfo?,
    onDeviceSelected: (AudioDeviceInfo) -> Unit,
    onSystemDefaultSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Audio Output / DAC",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                DeviceItem(
                    name = "System Default",
                    icon = Icons.Default.Speaker,
                    isSelected = selectedDevice == null,
                    onClick = onSystemDefaultSelected
                )
            }

            items(devices) { device ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    DeviceItem(
                        name = getDeviceName(device),
                        icon = getDeviceIcon(device),
                        isSelected = selectedDevice == device,
                        onClick = { onDeviceSelected(device) }
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceItem(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun getDeviceName(device: AudioDeviceInfo): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val label = device.productName.toString()
        if (label.isNotBlank()) label else "Unknown Device (${device.type})"
    } else {
        "Unknown Device"
    }
}

private fun getDeviceIcon(device: AudioDeviceInfo): ImageVector {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return when (device.type) {
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET, AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> Icons.Default.Headphones
            AudioDeviceInfo.TYPE_USB_DEVICE, AudioDeviceInfo.TYPE_USB_ACCESSORY, AudioDeviceInfo.TYPE_USB_HEADSET -> Icons.Default.Usb
            else -> Icons.Default.Speaker
        }
    }
    return Icons.Default.Speaker
}
