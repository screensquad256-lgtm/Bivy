package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.ABMode
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivySurfaceElevated

@Composable
fun ABToggleSwitch(
    currentMode: ABMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnhanced = currentMode == ABMode.ENHANCED

    val originalBg by animateColorAsState(
        targetValue = if (!isEnhanced) Color(0xFF334155) else Color.Transparent,
        label = "orig_bg"
    )
    val enhancedBg by animateColorAsState(
        targetValue = if (isEnhanced) BivyGold else Color.Transparent,
        label = "enh_bg"
    )
    val originalText by animateColorAsState(
        targetValue = if (!isEnhanced) Color.White else Color(0xFF64748B),
        label = "orig_txt"
    )
    val enhancedText by animateColorAsState(
        targetValue = if (isEnhanced) Color(0xFF1E1500) else Color(0xFF64748B),
        label = "enh_txt"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(BivySurfaceElevated)
            .border(1.dp, BivyBorder, RoundedCornerShape(24.dp))
            .clickable { onToggle() }
            .padding(3.dp)
            .testTag("ab_comparison_toggle")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(originalBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ORIGINAL",
                    color = originalText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(enhancedBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ENHANCED",
                    color = enhancedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
