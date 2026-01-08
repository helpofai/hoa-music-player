package com.helpofai.mymmusic.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.helpofai.mymmusic.R
import com.helpofai.mymmusic.media.EqBand
import com.helpofai.mymmusic.ui.MusicViewModel
import com.helpofai.mymmusic.ui.components.GlassySurface
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import com.helpofai.mymmusic.ui.components.PremiumCard
import com.helpofai.mymmusic.ui.theme.AppTheme

@Composable
fun EqScreen(
    viewModel: MusicViewModel,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.nav_equalizer),
        stringResource(R.string.output), // Or a specific soundstage string
        stringResource(R.string.nav_settings) // Or Fidelity
    )
    
    // Using explicit strings from resources for consistency
    val tabLabels = listOf(
        stringResource(R.string.nav_equalizer),
        "Soundstage", 
        "Fidelity"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.gradients.backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            HeaderWithMeters(viewModel, onBackClick)

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabLabels.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                when (selectedTab) {
                    0 -> EqualizerTab(viewModel)
                    1 -> SoundstageTab(viewModel)
                    2 -> FidelityTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun HeaderWithMeters(viewModel: MusicViewModel, onBackClick: () -> Unit) {
    val leftLevel by viewModel.leftLevel.collectAsState()
    val rightLevel by viewModel.rightLevel.collectAsState()
    val isEqEnabled by viewModel.isEqEnabled.collectAsState()

    GlassySurface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(stringResource(R.string.mastering_console), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.dsp_32bit), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }

            Row(modifier = Modifier.height(40.dp).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                VUMeter(level = leftLevel, label = "L")
                VUMeter(level = rightLevel, label = "R")
            }

            Switch(
                checked = isEqEnabled,
                onCheckedChange = { viewModel.setEqEnabled(it) },
                modifier = Modifier.graphicsLayer(scaleX = 0.8f, scaleY = 0.8f)
            )
        }
    }
}

@Composable
fun VUMeter(level: Float, label: String) {
    val animatedLevel by animateFloatAsState(level, label = "meter")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedLevel.coerceIn(0f, 1f))
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Red, Color.Yellow, Color.Green)
                        )
                    )
            )
        }
        Text(label, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EqualizerTab(viewModel: MusicViewModel) {
    val eqBands by viewModel.eqBands.collectAsState()
    
    Column {
        Text(stringResource(R.string.precision_eq), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        
        GlassySurface(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                eqBands.forEach { band ->
                    EqSlider(band = band) { viewModel.setEqBandLevel(band.id, it) }
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val bassStrength by viewModel.bassStrength.collectAsState()
            PremiumCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.impact_bass), style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = bassStrength.toFloat(),
                        onValueChange = { viewModel.setBassStrength(it.toInt().toShort()) },
                        valueRange = 0f..1000f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFCE93D8), activeTrackColor = Color(0xFFCE93D8))
                    )
                }
            }
            val virtStrength by viewModel.virtualizerStrength.collectAsState()
            PremiumCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.immersion_3d), style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = virtStrength.toFloat(),
                        onValueChange = { viewModel.setVirtualizerStrength(it.toInt().toShort()) },
                        valueRange = 0f..1000f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF80CBC4), activeTrackColor = Color(0xFF80CBC4))
                    )
                }
            }
        }
    }
}

@Composable
fun SoundstageTab(viewModel: MusicViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        val width by viewModel.stereoWidth.collectAsState()
        val balance by viewModel.stereoBalance.collectAsState()
        val crossfeed by viewModel.crossfeed.collectAsState()
        val is8DEnabled by viewModel.is8DEnabled.collectAsState()
        val rotationSpeed by viewModel.rotationSpeed.collectAsState()

        // 8D Audio Controls
        PremiumCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("8D Audio Mode", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text("Rotating spatial audio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = is8DEnabled,
                        onCheckedChange = { viewModel.set8DMode(it) }
                    )
                }
                
                if (is8DEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Rotation Speed", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = rotationSpeed,
                        onValueChange = { viewModel.set8DSpeed(it) },
                        valueRange = 0.05f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.secondary, activeTrackColor = MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }

        ProSliderRow(stringResource(R.string.stereo_width), width, 0f..2f, Color(0xFF64B5F6)) { viewModel.setStereoWidth(it) }
        ProSliderRow(stringResource(R.string.channel_balance), balance, -1f..1f, Color(0xFFBA68C8)) { viewModel.setStereoBalance(it) }
        ProSliderRow(stringResource(R.string.headphone_crossfeed), crossfeed, 0f..1f, Color(0xFFFF8A65)) { viewModel.setCrossfeed(it) }
        
        Spacer(Modifier.height(16.dp))
        Text(
            "Soundstage controls adjust the spatial positioning of instruments in the virtual 3D space.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FidelityTab(viewModel: MusicViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        val preAmp by viewModel.preAmp.collectAsState()
        val clarity by viewModel.clarity.collectAsState()
        val warmth by viewModel.warmth.collectAsState()
        val subBass by viewModel.subBass.collectAsState()
        val air by viewModel.hiFiAir.collectAsState()
        val loudness by viewModel.adaptiveLoudness.collectAsState()

        ProSliderRow(stringResource(R.string.pre_amp), preAmp, 0.5f..1.5f, Color(0xFF81C784)) { viewModel.setPreAmp(it) }
        ProSliderRow(stringResource(R.string.clarity_exciter), clarity, 0f..1f, Color(0xFF4FC3F7)) { viewModel.setClarity(it) }
        ProSliderRow(stringResource(R.string.warmth_tube), warmth, 0f..1f, Color(0xFFFFD54F)) { viewModel.setWarmth(it) }
        ProSliderRow(stringResource(R.string.virtual_sub), subBass, 0f..1f, Color(0xFFE57373)) { viewModel.setSubBass(it) }
        ProSliderRow(stringResource(R.string.hi_fi_air), air, 0f..1f, Color(0xFFAED581)) { viewModel.setHiFiAir(it) }
        ProSliderRow(stringResource(R.string.adaptive_loudness), loudness, 0f..1f, Color(0xFFF06292)) { viewModel.setAdaptiveLoudness(it) }
    }
}

@Composable
fun ProSliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, color: Color, onValueChange: (Float) -> Unit) {
    PremiumCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("%.2f".format(value), style = MaterialTheme.typography.labelSmall, color = color)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
            )
        }
    }
}

@Composable
fun EqSlider(
    band: EqBand,
    onLevelChange: (Short) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(48.dp).fillMaxHeight()
    ) {
        Text(
            text = formatFreq(band.centerFreq),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .width(200.dp)
                .rotate(-90f)
        ) {
            Slider(
                value = band.currentLevel.toFloat(),
                onValueChange = { onLevelChange(it.toInt().toShort()) },
                valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                 colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${band.currentLevel / 100}dB",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatFreq(milliHertz: Int): String {
    val hertz = milliHertz / 1000
    return if (hertz < 1000) "${hertz}Hz" else "${hertz / 1000}kHz"
}