package com.helpofai.mymmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.ui.theme.AppTheme

@Composable
fun MusicNavigationDrawer(
    onItemClick: (String) -> Unit,
    currentRoute: String?
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.width(300.dp)
    ) {
        // 1. Header with Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(AppTheme.gradients.primaryGradient)
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.pro_audio_suite),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Navigation Items
        DrawerItem(stringResource(R.string.nav_home), Icons.Default.Home, onItemClick)
        DrawerItem(stringResource(R.string.nav_library), Icons.Default.LibraryMusic, onItemClick)
        DrawerItem(stringResource(R.string.nav_folders), Icons.Default.Folder, onItemClick)
        DrawerItem(stringResource(R.string.nav_equalizer), Icons.Default.Equalizer, onItemClick)
        DrawerItem(stringResource(R.string.nav_sleep_timer), Icons.Default.Timer, onItemClick)
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
        
        DrawerItem(stringResource(R.string.nav_settings), Icons.Default.Settings, onItemClick)
        DrawerItem(stringResource(R.string.nav_about), Icons.Default.Info, onItemClick)
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(label) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
