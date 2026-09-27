package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.PlaybackMode
import com.example.ui.components.ABToggleSwitch
import com.example.ui.components.StudioSpectrumVisualizer
import com.example.ui.components.VisualizerMode
import com.example.ui.components.WaveformScrubber
import com.example.ui.theme.BivyBackground
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCrimson
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyEmerald
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivyGoldLight
import com.example.ui.theme.BivySurface
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary
import com.example.viewmodel.BivyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val positionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val abMode by viewModel.abMode.collectAsStateWithLifecycle()
    val playbackMode by viewModel.playbackMode.collectAsStateWithLifecycle()
    val visualizerSnapshot by viewModel.visualizerSnapshot.collectAsStateWithLifecycle()
    val currentPreset by viewModel.currentPreset.collectAsStateWithLifecycle()
    val activeQueue by viewModel.activeQueue.collectAsStateWithLifecycle()

    var showLyricsSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showSpeedPitchDialog by remember { mutableStateOf(false) }

    val song = currentSong

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF101624),
                        BivyBackground,
                        Color(0xFF06090F)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Back, Studio Title, Queue
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("now_playing_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "STUDIO PLAYBACK",
                        color = BivyGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "${song?.codec ?: "FLAC"} • ${song?.sampleRateHz ?: 48000} Hz",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = { showQueueSheet = true },
                    modifier = Modifier.testTag("now_playing_queue")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Artwork Core with Studio Vignette
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .shadow(32.dp, RoundedCornerShape(28.dp), spotColor = BivyGold.copy(alpha = 0.35f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF263346),
                                Color(0xFF141A24),
                                Color(0xFF0A0E15)
                            )
                        )
                    )
                    .border(1.5.dp, BivyGold.copy(alpha = 0.6f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Audio visualizer aura in center of art
                StudioSpectrumVisualizer(
                    snapshot = visualizerSnapshot,
                    isPlaying = isPlaying,
                    mode = VisualizerMode.SPECTRUM,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(120.dp)
                )

                // Vinyl icon badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC090D14))
                        .border(1.dp, BivyGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = "Studio Track",
                        tint = BivyGold,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Hi-Res Audio Stamp
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xE6090D14))
                        .border(1.dp, BivyEmerald, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (song?.isLossless == true) "LOSSLESS" else "HI-RES",
                        color = BivyEmerald,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Track Title, Artist, and Favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song?.title ?: "No track selected",
                        color = BivyTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${song?.artist ?: "Unknown"} — ${song?.album ?: ""}",
                        color = BivyTextMuted,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { song?.let { viewModel.toggleFavorite(it) } },
                    modifier = Modifier.testTag("now_playing_fav_btn")
                ) {
                    Icon(
                        imageVector = if (song?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (song?.isFavorite == true) BivyCrimson else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Waveform Scrubber
            WaveformScrubber(
                currentPositionMs = positionMs,
                durationMs = durationMs,
                isPlaying = isPlaying,
                onSeek = { viewModel.seekTo(it) }
            )

            // Time stamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(positionMs),
                    color = BivyTextMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = formatMs(durationMs),
                    color = BivyTextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Master Playback Transport Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Shuffle / Repeat Mode
                IconButton(
                    onClick = { viewModel.togglePlaybackMode() },
                    modifier = Modifier.testTag("now_playing_mode_btn")
                ) {
                    val modeIcon = when (playbackMode) {
                        PlaybackMode.REPEAT_ALL -> Icons.Default.Repeat
                        PlaybackMode.REPEAT_ONE -> Icons.Default.RepeatOne
                        PlaybackMode.SHUFFLE -> Icons.Default.Shuffle
                    }
                    Icon(
                        imageVector = modeIcon,
                        contentDescription = "Playback Mode",
                        tint = if (playbackMode != PlaybackMode.REPEAT_ALL) BivyGold else Color.White
                    )
                }

                // Previous
                IconButton(
                    onClick = { viewModel.playPrevious() },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("now_playing_prev_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause (Large Floating Studio Orb)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(20.dp, CircleShape, spotColor = BivyGold)
                        .clip(CircleShape)
                        .background(BivyGold)
                        .clickable { viewModel.togglePlayPause() }
                        .testTag("now_playing_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF1E1500),
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = { viewModel.playNext() },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("now_playing_next_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Lyrics
                IconButton(
                    onClick = { showLyricsSheet = true },
                    modifier = Modifier.testTag("now_playing_lyrics_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lyrics,
                        contentDescription = "Lyrics",
                        tint = BivyCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Studio Toolbar: A/B Switch, Speed/Pitch, Audio Lab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BivySurface)
                    .border(1.dp, BivyBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Audio A/B Switch
                ABToggleSwitch(
                    currentMode = abMode,
                    onToggle = { viewModel.toggleABMode() }
                )

                // Speed / Pitch Trigger
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showSpeedPitchDialog = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed & Pitch",
                        tint = BivyCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.2f", currentPreset?.speedRatio ?: 1.0f)}x",
                        color = BivyTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Direct Audio Lab shortcut
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(Screen.AUDIO_LAB) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Open Audio Lab",
                        tint = BivyGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Audio Lab",
                        color = BivyGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Lyrics Bottom Sheet
        if (showLyricsSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showLyricsSheet = false },
                sheetState = sheetState,
                containerColor = BivySurfaceElevated,
                contentColor = BivyTextPrimary
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "SYNCHRONIZED STUDIO LYRICS",
                        color = BivyGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    val lyricsText = song?.lyrics ?: "No synchronized lyrics available for this local file.\nYou can place a .lrc file in the same folder or edit lyrics manually."

                    Text(
                        text = lyricsText,
                        color = BivyTextPrimary,
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Queue Bottom Sheet
        if (showQueueSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showQueueSheet = false },
                sheetState = sheetState,
                containerColor = BivySurfaceElevated,
                contentColor = BivyTextPrimary
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PLAYBACK QUEUE (${activeQueue.size})",
                            color = BivyGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            Text(
                                text = "Shuffle",
                                color = BivyCyan,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .clickable { viewModel.shuffleQueue() }
                                    .padding(8.dp)
                            )
                            Text(
                                text = "Clear",
                                color = BivyCrimson,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .clickable { viewModel.clearQueue() }
                                    .padding(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    activeQueue.forEachIndexed { index, queueSong ->
                        val isCurrent = queueSong.id == song?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) Color(0xFF1E2838) else Color.Transparent)
                                .clickable { viewModel.playSong(queueSong, activeQueue) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = if (isCurrent) BivyGold else BivyTextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.width(28.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = queueSong.title,
                                    color = if (isCurrent) BivyGold else BivyTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = queueSong.artist,
                                    color = BivyTextMuted,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = formatMs(queueSong.durationMs),
                                color = BivyTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Speed & Pitch Dialog
        if (showSpeedPitchDialog) {
            val preset = currentPreset
            if (preset != null) {
                var speedVal by remember { mutableStateOf(preset.speedRatio) }
                var pitchVal by remember { mutableStateOf(preset.pitchRatio) }

                ModalBottomSheet(
                    onDismissRequest = {
                        viewModel.updateCurrentPreset(
                            preset.copy(speedRatio = speedVal, pitchRatio = pitchVal)
                        )
                        showSpeedPitchDialog = false
                    },
                    containerColor = BivySurfaceElevated,
                    contentColor = BivyTextPrimary
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "PLAYBACK SPEED & PITCH",
                            color = BivyGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Speed
                        Text(
                            text = "Speed: ${String.format(Locale.US, "%.2f", speedVal)}x",
                            color = BivyTextPrimary,
                            fontSize = 14.sp
                        )
                        Slider(
                            value = speedVal,
                            onValueChange = { speedVal = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = BivyCyan,
                                activeTrackColor = BivyCyan
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pitch
                        Text(
                            text = "Pitch: ${String.format(Locale.US, "%.2f", pitchVal)}x",
                            color = BivyTextPrimary,
                            fontSize = 14.sp
                        )
                        Slider(
                            value = pitchVal,
                            onValueChange = { pitchVal = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = BivyGold,
                                activeTrackColor = BivyGold
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Reset to 1.0x",
                                color = BivyTextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .clickable {
                                        speedVal = 1.0f
                                        pitchVal = 1.0f
                                    }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Apply",
                                color = BivyGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.updateCurrentPreset(
                                            preset.copy(speedRatio = speedVal, pitchRatio = pitchVal)
                                        )
                                        showSpeedPitchDialog = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
