package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.ABToggleSwitch
import com.example.ui.components.MiniStudioBar
import com.example.ui.components.SoundOrbitCanvas
import com.example.ui.theme.BivyBackground
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivySurface
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary
import com.example.ui.theme.BivyViolet
import com.example.viewmodel.BivyViewModel
import com.example.viewmodel.Screen

@Composable
fun AudioSpaceScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val abMode by viewModel.abMode.collectAsStateWithLifecycle()
    val currentPreset by viewModel.currentPreset.collectAsStateWithLifecycle()
    val currentDeviceType by viewModel.currentDeviceType.collectAsStateWithLifecycle()
    val visualizerSnapshot by viewModel.visualizerSnapshot.collectAsStateWithLifecycle()
    val voiceFeedback by viewModel.voiceFeedback.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF090D14),
                        BivyBackground,
                        Color(0xFF07090E)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Luxury Studio Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BIVY",
                            color = BivyGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BivyBorder)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STUDIO",
                                color = BivyCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    Text(
                        text = "Your Music. Your Sound. Your Studio.",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }

                // Quick Header Actions: Car Mode, Voice, Privacy
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.CAR_MODE) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("header_car_mode_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Car Mode",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleVoiceListening() },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("header_voice_cmd_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Control",
                            tint = BivyCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.PRIVACY_CENTER) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("header_privacy_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Private Audio",
                            tint = BivyTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.PRO_STORE) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("header_pro_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Pro Studio",
                            tint = BivyGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Voice Command Feedback Banner (if active)
            AnimatedVisibility(
                visible = voiceFeedback != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                voiceFeedback?.let { feedback ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2838))
                            .border(1.dp, BivyCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = feedback,
                            color = BivyCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Device Profile & Master A/B Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Device Profile Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(BivySurfaceElevated)
                        .border(1.dp, BivyBorder, RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.AUDIO_LAB) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (currentDeviceType) {
                        AudioDeviceType.HEADPHONES, AudioDeviceType.EARBUDS -> Icons.Default.Headphones
                        AudioDeviceType.CAR -> Icons.Default.DirectionsCar
                        else -> Icons.Default.Speaker
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Device Type",
                        tint = BivyGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentPreset?.name ?: "Studio Master",
                        color = BivyTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // A/B Comparison Master Switch
                ABToggleSwitch(
                    currentMode = abMode,
                    onToggle = { viewModel.toggleABMode() }
                )
            }

            // Central Hero: THE SOUND ORBIT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SoundOrbitCanvas(
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    onCenterClick = {
                        if (currentSong != null) {
                            viewModel.navigateTo(Screen.NOW_PLAYING)
                        } else {
                            viewModel.togglePlayPause()
                        }
                    },
                    onNodeClick = { ring ->
                        viewModel.selectOrbitRing(ring)
                    }
                )
            }

            // Quick Studio Control Bar (Launchers for Audio Lab, VibeMix, My Sound)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStudioAction(
                    icon = Icons.Default.GraphicEq,
                    label = "Audio Lab",
                    accent = BivyCyan,
                    onClick = { viewModel.navigateTo(Screen.AUDIO_LAB) }
                )
                QuickStudioAction(
                    icon = Icons.Default.AutoAwesome,
                    label = "VibeMix",
                    accent = BivyViolet,
                    onClick = { viewModel.navigateTo(Screen.VIBEMIX) }
                )
                QuickStudioAction(
                    icon = Icons.Default.Headphones,
                    label = "My Sound",
                    accent = BivyGold,
                    onClick = { viewModel.navigateTo(Screen.MY_SOUND) }
                )
            }

            // Bottom Docked Mini Player
            MiniStudioBar(
                currentSong = currentSong,
                isPlaying = isPlaying,
                abMode = abMode,
                visualizerSnapshot = visualizerSnapshot,
                onBarClick = { viewModel.navigateTo(Screen.NOW_PLAYING) },
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onSkipNextClick = { viewModel.playNext() }
            )
        }
    }
}

@Composable
private fun QuickStudioAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    accent: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BivySurface)
            .border(1.dp, BivyBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = accent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = BivyTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
