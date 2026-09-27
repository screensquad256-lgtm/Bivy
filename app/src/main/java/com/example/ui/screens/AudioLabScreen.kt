package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AudioDeviceType
import com.example.model.AudioPreset
import com.example.ui.components.ABToggleSwitch
import com.example.ui.theme.BivyBackground
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCrimson
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyEmerald
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivySurface
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary
import com.example.viewmodel.BivyViewModel
import java.util.Locale

@Composable
fun AudioLabScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val currentPreset by viewModel.currentPreset.collectAsStateWithLifecycle()
    val allPresets by viewModel.allPresets.collectAsStateWithLifecycle()
    val abMode by viewModel.abMode.collectAsStateWithLifecycle()
    val currentDeviceType by viewModel.currentDeviceType.collectAsStateWithLifecycle()

    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }

    val preset = currentPreset ?: allPresets.firstOrNull()

    val freqLabels = remember {
        listOf("31Hz", "62Hz", "125Hz", "250Hz", "500Hz", "1kHz", "2kHz", "4kHz", "8kHz", "16kHz")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F1622),
                        BivyBackground,
                        Color(0xFF080B10)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("audio_lab_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AUDIO LAB",
                        color = BivyGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Hardware DSP & Master Studio Chain",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.testTag("audio_lab_save_preset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Save Custom Preset",
                        tint = BivyCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Master A/B Comparison Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BivySurface)
                    .border(1.dp, BivyBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "A/B COMPARISON",
                        color = BivyGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Compare Original vs Processed DSP",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }
                ABToggleSwitch(
                    currentMode = abMode,
                    onToggle = { viewModel.toggleABMode() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets Horizontal Reel
            Text(
                text = "STUDIO PRESETS",
                color = BivyTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allPresets.forEach { p ->
                    val isSelected = preset?.id == p.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BivyGold else BivySurfaceElevated)
                            .border(1.dp, if (isSelected) BivyGold else BivyBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.setAudioPreset(p) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = p.name,
                                color = if (isSelected) Color(0xFF1E1500) else BivyTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            if (!p.isSystem) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Custom Preset",
                                    tint = if (isSelected) Color(0xFF1E1500) else BivyCrimson,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { viewModel.deletePreset(p.id) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 10-Band Graphic Equalizer
            if (preset != null) {
                val bandLevels = preset.getBandLevels().toMutableList()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BivySurface)
                        .border(1.dp, BivyBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "10-BAND MASTER EQUALIZER",
                                color = BivyGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "±12 dB",
                                color = BivyTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sliders Grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            freqLabels.forEachIndexed { idx, label ->
                                val level = if (idx < bandLevels.size) bandLevels[idx] else 0

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(38.dp)
                                ) {
                                    Text(
                                        text = "${if (level > 0) "+$level" else "$level"}",
                                        color = if (level != 0) BivyGold else BivyTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    // Vertical slider represented via custom height/touch
                                    Slider(
                                        value = level.toFloat(),
                                        onValueChange = { newVal ->
                                            val updatedBands = bandLevels.toMutableList()
                                            while (updatedBands.size <= idx) updatedBands.add(0)
                                            updatedBands[idx] = newVal.toInt()
                                            val newString = updatedBands.joinToString(",")
                                            viewModel.updateCurrentPreset(
                                                preset.copy(bandLevelsString = newString)
                                            )
                                        },
                                        valueRange = -12f..12f,
                                        modifier = Modifier
                                            .width(130.dp)
                                            .padding(vertical = 4.dp),
                                        colors = SliderDefaults.colors(
                                            thumbColor = BivyGold,
                                            activeTrackColor = BivyGold
                                        )
                                    )

                                    Text(
                                        text = label,
                                        color = BivyTextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DSP Processing Sliders: Bass Boost, Virtualizer, Loudness, Stereo Width
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BivySurface)
                        .border(1.dp, BivyBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "AUDIO PROCESSING & DSP",
                            color = BivyCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bass Boost
                        StudioDspSlider(
                            label = "Bass Boost",
                            valueText = "${preset.bassBoostStrength / 10}%",
                            value = (preset.bassBoostStrength / 1000f),
                            onValueChange = {
                                viewModel.updateCurrentPreset(
                                    preset.copy(bassBoostStrength = (it * 1000).toInt())
                                )
                            },
                            accentColor = BivyGold
                        )

                        // Virtualizer 3D
                        StudioDspSlider(
                            label = "3D Virtualizer",
                            valueText = "${preset.virtualizerStrength / 10}%",
                            value = (preset.virtualizerStrength / 1000f),
                            onValueChange = {
                                viewModel.updateCurrentPreset(
                                    preset.copy(virtualizerStrength = (it * 1000).toInt())
                                )
                            },
                            accentColor = BivyCyan
                        )

                        // Loudness Enhancer
                        StudioDspSlider(
                            label = "Loudness Maximizer",
                            valueText = "+${preset.loudnessGainMb / 100} dB",
                            value = (preset.loudnessGainMb / 1000f),
                            onValueChange = {
                                viewModel.updateCurrentPreset(
                                    preset.copy(loudnessGainMb = (it * 1000).toInt())
                                )
                            },
                            accentColor = BivyEmerald
                        )

                        // Stereo Balance
                        StudioDspSlider(
                            label = "Stereo Balance",
                            valueText = if (preset.stereoBalance == 0f) "Center" else if (preset.stereoBalance < 0) "L ${(kotlin.math.abs(preset.stereoBalance) * 100).toInt()}%" else "R ${(preset.stereoBalance * 100).toInt()}%",
                            value = (preset.stereoBalance + 1f) / 2f,
                            onValueChange = {
                                val bal = (it * 2f) - 1f
                                viewModel.updateCurrentPreset(preset.copy(stereoBalance = bal))
                            },
                            accentColor = Color(0xFF64B5F6)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reverb Space Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BivySurface)
                        .border(1.dp, BivyBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "ACOUSTIC REVERB SPACE",
                            color = BivyGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val reverbRooms = listOf(
                            "NONE" to "Dry (Bypass)",
                            "SMALL_ROOM" to "Acoustic Booth",
                            "MEDIUM_ROOM" to "Studio Live Room",
                            "LARGE_ROOM" to "Chamber",
                            "MEDIUM_HALL" to "Concert Hall",
                            "LARGE_HALL" to "Cathedral Space",
                            "PLATE" to "Vintage Plate"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            reverbRooms.forEach { (code, name) ->
                                val isSelected = preset.reverbPreset == code
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) BivyCyan else BivySurfaceElevated)
                                        .clickable {
                                            viewModel.updateCurrentPreset(preset.copy(reverbPreset = code))
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = name,
                                        color = if (isSelected) Color(0xFF00363D) else BivyTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bind Preset to Audio Profile
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BivySurface)
                        .border(1.dp, BivyBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "ASSIGN TO HARDWARE PROFILE",
                            color = BivyTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Auto-activate this EQ preset whenever connecting this device",
                            color = BivyTextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AudioDeviceType.values().forEach { dev ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BivySurfaceElevated)
                                        .border(1.dp, BivyBorder, RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.setProfileForDevice(dev, preset.id)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Bind ${dev.name.replace("_", " ")}",
                                        color = BivyCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Save Custom Preset Dialog
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = {
                    Text(
                        text = "Save Custom Preset",
                        color = BivyGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Enter a name for this custom audio studio configuration:",
                            color = BivyTextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPresetName,
                            onValueChange = { newPresetName = it },
                            placeholder = { Text("e.g. My Warm Vinyl") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPresetName.isNotBlank()) {
                                viewModel.saveCustomPreset(newPresetName.trim())
                                newPresetName = ""
                                showSaveDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BivyGold)
                    ) {
                        Text("Save Preset", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancel", color = BivyTextMuted)
                    }
                },
                containerColor = BivySurfaceElevated
            )
        }
    }
}

@Composable
private fun StudioDspSlider(
    label: String,
    valueText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    accentColor: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = BivyTextPrimary, fontSize = 13.sp)
            Text(text = valueText, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor
            )
        )
    }
}
