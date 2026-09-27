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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
fun VibeMixScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle()

    var selectedMood by remember { mutableStateOf("Relax") }
    var selectedActivity by remember { mutableStateOf("Night Drive") }
    var selectedEnergy by remember { mutableStateOf("Medium") }

    val moods = remember { listOf("Relax", "Energetic", "Focus", "Melancholic", "Dark", "Euphoric") }
    val activities = remember { listOf("Workout", "Study", "Night Drive", "Travel", "Rain", "Morning", "Coding") }
    val energies = remember { listOf("Low", "Medium", "High") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF140D24),
                        BivyBackground,
                        Color(0xFF090812)
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
                    modifier = Modifier.testTag("vibemix_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VIBEMIX",
                            color = BivyViolet,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "VibeMix",
                            tint = BivyViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Intelligent Local Acoustic Engine",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Explanation card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BivySurface)
                    .border(1.dp, BivyBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "VibeMix analyzes local audio acoustic tags, tempo, duration, and listening frequency directly on your device. Zero cloud uploading, 100% private.",
                    color = BivyTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Select Mood
            Text(
                text = "1. SELECT MOOD",
                color = BivyGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.forEach { mood ->
                    val isSel = selectedMood == mood
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) BivyViolet else BivySurfaceElevated)
                            .border(1.dp, if (isSel) BivyViolet else BivyBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedMood = mood }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = mood,
                            color = if (isSel) Color.White else BivyTextPrimary,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Select Activity
            Text(
                text = "2. SELECT ACTIVITY",
                color = BivyCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activities.forEach { act ->
                    val isSel = selectedActivity == act
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) BivyCyan else BivySurfaceElevated)
                            .border(1.dp, if (isSel) BivyCyan else BivyBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedActivity = act }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = act,
                            color = if (isSel) Color(0xFF00363D) else BivyTextPrimary,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Select Energy
            Text(
                text = "3. ENERGY LEVEL",
                color = Color(0xFFFFB74D),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                energies.forEach { eng ->
                    val isSel = selectedEnergy == eng
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) Color(0xFFFFB74D) else BivySurfaceElevated)
                            .border(1.dp, if (isSel) Color(0xFFFFB74D) else BivyBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedEnergy = eng }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = eng,
                                tint = if (isSel) Color(0xFF332000) else BivyTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = eng,
                                color = if (isSel) Color(0xFF332000) else BivyTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Generate Button
            Button(
                onClick = {
                    viewModel.generateVibeMix(selectedMood, selectedActivity, selectedEnergy)
                    viewModel.navigateTo(Screen.NOW_PLAYING)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("generate_vibemix_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = BivyViolet)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Generate VibeMix",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate \"$selectedMood • $selectedActivity\"",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
