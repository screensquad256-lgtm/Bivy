package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.ABMode
import com.example.audio.VisualizerSnapshot
import com.example.model.Song
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyEmerald
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary

@Composable
fun MiniStudioBar(
    currentSong: Song?,
    isPlaying: Boolean,
    abMode: ABMode,
    visualizerSnapshot: VisualizerSnapshot,
    onBarClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = BivyGold.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        BivySurfaceElevated,
                        Color(0xFF161F2E)
                    )
                )
            )
            .border(1.dp, BivyBorder, RoundedCornerShape(20.dp))
            .clickable { onBarClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("mini_studio_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Live mini spectrum visualizer indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF090D14))
                    .border(1.dp, if (isPlaying) BivyGold else BivyBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                StudioSpectrumVisualizer(
                    snapshot = visualizerSnapshot,
                    isPlaying = isPlaying,
                    mode = VisualizerMode.SPECTRUM,
                    modifier = Modifier.size(34.dp, 24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Track info and studio tags
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = currentSong.title,
                    color = BivyTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentSong.artist,
                        color = BivyTextMuted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•",
                        color = BivyTextMuted,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (currentSong.isLossless) BivyEmerald.copy(alpha = 0.15f) else BivyCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = currentSong.codec,
                            color = if (currentSong.isLossless) BivyEmerald else BivyCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (abMode == ABMode.ENHANCED) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BivyGold.copy(alpha = 0.18f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "DSP",
                                color = BivyGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Transport Controls
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BivyGold)
                        .testTag("mini_player_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color(0xFF1E1500),
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onSkipNextClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("mini_player_skip_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
