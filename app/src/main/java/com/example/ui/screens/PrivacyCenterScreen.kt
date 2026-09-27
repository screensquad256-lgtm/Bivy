package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.BivyBackground
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyEmerald
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivySurface
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary
import com.example.viewmodel.BivyViewModel

@Composable
fun PrivacyCenterScreen(
    viewModel: BivyViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var appLockEnabled by remember { mutableStateOf(false) }
    var hideFolderEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0C1820),
                        BivyBackground,
                        Color(0xFF06090E)
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
                    modifier = Modifier.testTag("privacy_back")
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
                            text = "PRIVATE AUDIO",
                            color = BivyCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = BivyCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Zero-Cloud Guarantee & Privacy Controls",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Security Promise Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0F2532), Color(0xFF0D1B24))
                        )
                    )
                    .border(1.dp, BivyCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = BivyEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LOCAL-FIRST INTEGRITY",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your music files, metadata, listening analytics, EQ presets, and playlists never leave your device. Bivy operates 100% offline without telemetry tracking.",
                        color = BivyTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy Pillars
            Text(
                text = "CORE ARCHITECTURE PRINCIPLES",
                color = BivyGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            PrivacyPillarRow(
                icon = Icons.Default.NoAccounts,
                title = "No Account Required",
                desc = "You will never be forced to log in, create a profile, or share your identity."
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrivacyPillarRow(
                icon = Icons.Default.CloudOff,
                title = "Zero Audio Cloud Uploads",
                desc = "Songs are played directly via the local Android media framework from your storage."
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrivacyPillarRow(
                icon = Icons.Default.FolderSpecial,
                title = "Private Media Sandboxing",
                desc = "Media scanner respects hidden .nomedia folders and user storage selections."
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Toggles: App Lock & Hidden Folders
            Text(
                text = "ACCESS & DEVICE PROTECTION",
                color = BivyTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BivySurface)
                    .border(1.dp, BivyBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "App Lock / Biometric Protection",
                                color = BivyTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Require fingerprint or passcode on launch",
                                color = BivyTextMuted,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = appLockEnabled,
                            onCheckedChange = { appLockEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BivyGold,
                                checkedTrackColor = BivyBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Exclude System Notification Sounds",
                                color = BivyTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Ignore ringtones, alarms, and short voice memos",
                                color = BivyTextMuted,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = hideFolderEnabled,
                            onCheckedChange = { hideFolderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BivyCyan,
                                checkedTrackColor = BivyBorder
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrivacyPillarRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BivySurface)
            .border(1.dp, BivyBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF16202C)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = BivyCyan,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = BivyTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = BivyTextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
