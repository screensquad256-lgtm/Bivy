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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.theme.BivyGoldLight
import com.example.ui.theme.BivySurface
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary
import com.example.viewmodel.BivyViewModel

enum class ProTier(val title: String, val price: String, val badge: String?) {
    MONTHLY("Pro Monthly", "₹79 / month", null),
    YEARLY("Pro Yearly", "₹499 / year", "SAVE 47%"),
    LIFETIME("Pro Lifetime Studio", "₹1,199 one-time", "BEST VALUE")
}

@Composable
fun ProStoreScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val isProSubscriber by viewModel.isProSubscriber.collectAsStateWithLifecycle()
    var selectedTier by remember { mutableStateOf(ProTier.YEARLY) }

    val proFeatures = remember {
        listOf(
            "Hardware DSP & 10/31-Band Master Studio Equalizer",
            "Instant A/B Audio Comparison (Original vs Enhanced)",
            "VibeMix Generative Playlist Engine (Mood + Activity + Energy)",
            "3D Virtualizer & Acoustic Reverb Chamber Modeling",
            "Stereo Width & Balance Matrix Control",
            "Smart Hardware Profiles (Earbuds, Over-Ear, Car, Living Room)",
            "Pitch Shifting & Precision Playback Speed Controls",
            "Unlimited Custom Audio Presets & Export",
            "100% Ad-Free, Offline-First Architecture"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF221A08),
                        BivyBackground,
                        Color(0xFF090A0E)
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("pro_store_back")
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
                            text = "BIVY PRO STUDIO",
                            color = BivyGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Pro",
                            tint = BivyGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Professional Master Audio Suite",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Current Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isProSubscriber) BivyGold else BivySurfaceElevated)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { viewModel.toggleProSubscriber() }
            ) {
                Text(
                    text = if (isProSubscriber) "PRO STUDIO ACTIVE (UNLOCKED)" else "FREE TIER (TAP TO TOGGLE PRO)",
                    color = if (isProSubscriber) Color(0xFF1E1500) else BivyGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pricing Plans
            ProTier.values().forEach { tier ->
                val isSelected = selectedTier == tier
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFF1E2838) else BivySurface)
                        .border(
                            1.5.dp,
                            if (isSelected) BivyGold else BivyBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedTier = tier }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tier.title,
                                    color = if (isSelected) BivyGold else BivyTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                tier.badge?.let { badge ->
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(BivyGold)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = badge,
                                            color = Color(0xFF1E1500),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tier.price,
                                color = BivyTextMuted,
                                fontSize = 13.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) BivyGold else Color.Transparent)
                                .border(1.5.dp, if (isSelected) BivyGold else BivyBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF1E1500),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Features Checklist
            Text(
                text = "EVERYTHING INCLUDED IN PRO",
                color = BivyGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))

            proFeatures.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Included",
                        tint = BivyCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = feature,
                        color = BivyTextPrimary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Button
            Button(
                onClick = {
                    if (!isProSubscriber) viewModel.toggleProSubscriber()
                    viewModel.navigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("subscribe_pro_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = BivyGold)
            ) {
                Text(
                    text = if (isProSubscriber) "Manage Subscription" else "Activate ${selectedTier.title}",
                    color = Color(0xFF1E1500),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
