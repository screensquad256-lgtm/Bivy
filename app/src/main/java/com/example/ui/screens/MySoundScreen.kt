package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BivyBackground
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyEmerald
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivySurface
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary
import com.example.ui.theme.BivyViolet
import com.example.viewmodel.BivyViewModel
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun MySoundScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val mostPlayed by viewModel.mostPlayed.collectAsStateWithLifecycle()
    val totalTimeMs by viewModel.totalListeningTimeMs.collectAsStateWithLifecycle()
    val favoriteSongs by viewModel.favoriteSongs.collectAsStateWithLifecycle()

    val topSong = mostPlayed.firstOrNull()
    val topArtist = mostPlayed.groupBy { it.artist }.maxByOrNull { it.value.size }?.key ?: "Solaris Architecture"
    val topAlbum = mostPlayed.groupBy { it.album }.maxByOrNull { it.value.size }?.key ?: "Kinetic Atmosphere Vol. I"
    val topGenre = mostPlayed.groupBy { it.genre }.maxByOrNull { it.value.size }?.key ?: "Atmospheric Ambient"

    val hours = ((totalTimeMs ?: 2450000L) / (1000 * 60 * 60))
    val minutes = (((totalTimeMs ?: 2450000L) / (1000 * 60)) % 60)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F1626),
                        BivyBackground,
                        Color(0xFF070B12)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("my_sound_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "MY SOUND",
                        color = BivyGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Private Local Music Memory",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Total Time Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1E283C), Color(0xFF131A26))
                        )
                    )
                    .border(1.dp, BivyGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "TOTAL LISTENING TIME",
                            color = BivyGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${hours}h ${minutes}m",
                            color = BivyTextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "100% private on this device",
                            color = BivyTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = BivyGold,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Studio Milestones 2x2 Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Top Artist",
                    value = topArtist,
                    icon = Icons.Default.Person,
                    accent = BivyCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Top Album",
                    value = topAlbum,
                    icon = Icons.Default.Album,
                    accent = BivyViolet,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Top Genre",
                    value = topGenre,
                    icon = Icons.Default.Category,
                    accent = BivyEmerald,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Favorites",
                    value = "${favoriteSongs.size} Songs",
                    icon = Icons.Default.MusicNote,
                    accent = BivyGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Most Played Songs List
            Text(
                text = "MOST PLAYED TRACKS",
                color = BivyGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            mostPlayed.take(5).forEachIndexed { idx, s ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BivySurface)
                        .clickable {
                            viewModel.playSong(s, mostPlayed)
                            viewModel.navigateTo(Screen.NOW_PLAYING)
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${idx + 1}",
                        color = BivyGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.title,
                            color = BivyTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${s.artist} • ${s.playCount} plays",
                            color = BivyTextMuted,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BivySurface)
            .border(1.dp, BivyBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(Locale.US),
                    color = BivyTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = BivyTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
