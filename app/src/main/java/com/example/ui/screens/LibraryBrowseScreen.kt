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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.MiniStudioBar
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
import com.example.viewmodel.LibraryFilter
import com.example.viewmodel.Screen
import java.util.Locale

@Composable
fun LibraryBrowseScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val displayedSongs by viewModel.displayedSongs.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val abMode by viewModel.abMode.collectAsStateWithLifecycle()
    val visualizerSnapshot by viewModel.visualizerSnapshot.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F1420),
                        BivyBackground,
                        Color(0xFF070910)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Back, Search, and Rescan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("library_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "LOCAL MEDIA LIBRARY",
                    color = BivyGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = { viewModel.scanDeviceMusic() },
                    modifier = Modifier.testTag("library_rescan")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rescan Device Storage",
                        tint = BivyCyan
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("library_search_input"),
                placeholder = {
                    Text("Search song, artist, album, genre...", color = BivyTextMuted, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = BivyGold
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = BivyTextMuted
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BivyGold,
                    unfocusedBorderColor = BivyBorder,
                    focusedContainerColor = BivySurface,
                    unfocusedContainerColor = BivySurface
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LibraryFilter.values().forEach { filter ->
                    val isSel = activeFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) BivyGold else BivySurfaceElevated)
                            .border(1.dp, if (isSel) BivyGold else BivyBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.setActiveFilter(filter) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = filter.displayName,
                            color = if (isSel) Color(0xFF1E1500) else BivyTextPrimary,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Songs List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(displayedSongs, key = { _, song -> song.id }) { index, s ->
                    val isCurrent = s.id == currentSong?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCurrent) Color(0xFF182232) else BivySurface)
                            .border(
                                1.dp,
                                if (isCurrent) BivyGold.copy(alpha = 0.6f) else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                viewModel.playSong(s, displayedSongs)
                                viewModel.navigateTo(Screen.NOW_PLAYING)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Index or Playing Icon
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) BivyGold else Color(0xFF18202E)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent && isPlaying) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Playing",
                                    tint = Color(0xFF1E1500),
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isCurrent) Color(0xFF1E1500) else BivyTextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Title, Artist, Codec
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.title,
                                color = if (isCurrent) BivyGold else BivyTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = s.artist,
                                    color = BivyTextMuted,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "•", color = BivyTextMuted, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = s.codec,
                                    color = if (s.isLossless) BivyEmerald else BivyCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Duration
                        Text(
                            text = formatMs(s.durationMs),
                            color = BivyTextMuted,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Favorite Icon
                        IconButton(
                            onClick = { viewModel.toggleFavorite(s) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (s.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (s.isFavorite) BivyCrimson else BivyTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Bottom Mini Player
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

private fun formatMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
