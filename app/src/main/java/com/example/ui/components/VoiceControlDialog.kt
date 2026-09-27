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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyTextMuted
import com.example.ui.theme.BivyTextPrimary

@Composable
fun VoiceControlDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onCommand: (String) -> Unit
) {
    if (!isOpen) return

    var commandInput by remember { mutableStateOf("") }

    val quickCommands = remember {
        listOf(
            "Next song",
            "Turn bass up",
            "Play my favorites",
            "Rock preset",
            "Enable VibeMix",
            "Pause"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF162538)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Control",
                        tint = BivyCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VOICE STUDIO",
                        color = BivyGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "On-device private speech control",
                        color = BivyTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    placeholder = { Text("Speak or type command...", color = BivyTextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_command_input")
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Quick Studio Commands:",
                    color = BivyTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickCommands.forEach { cmd ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF182230))
                                .border(1.dp, BivyBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    onCommand(cmd)
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "“$cmd”",
                                color = BivyCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        onCommand(commandInput.trim())
                    }
                    onDismiss()
                }
            ) {
                Text("Execute", color = BivyGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BivyTextMuted)
            }
        },
        containerColor = BivySurfaceElevated
    )
}
