package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivyGoldLight
import kotlin.math.sin

@Composable
fun WaveformScrubber(
    currentPositionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }

    val progressFraction = if (isDragging) {
        dragFraction
    } else {
        if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
    }

    val barCount = 42

    // Pre-calculate pseudo-waveform amplitudes based on index
    val barHeights = remember {
        FloatArray(barCount) { i ->
            val angle = i * 0.45f
            (0.2f + 0.35f * kotlin.math.abs(sin(angle)) + 0.35f * kotlin.math.abs(sin(angle * 2.3f))).coerceIn(0.15f, 1.0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 6.dp)
            .testTag("waveform_scrubber")
            .pointerInput(durationMs) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    val targetMs = (fraction * durationMs).toLong()
                    onSeek(targetMs)
                }
            }
            .pointerInput(durationMs) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragFraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        isDragging = false
                        val targetMs = (dragFraction * durationMs).toLong()
                        onSeek(targetMs)
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        dragFraction = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            val width = size.width
            val height = size.height
            val totalBars = barHeights.size
            val barSpacing = 3.dp.toPx()
            val totalSpacing = (totalBars - 1) * barSpacing
            val barWidth = ((width - totalSpacing) / totalBars).coerceAtLeast(2.dp.toPx())

            val activeWidth = width * progressFraction

            for (i in 0 until totalBars) {
                val x = i * (barWidth + barSpacing)
                val barProgress = x / width
                val isPlayed = barProgress <= progressFraction

                val barH = height * barHeights[i]
                val top = (height - barH) / 2f

                val barColor = when {
                    isPlayed -> if (isDragging) BivyCyan else BivyGold
                    else -> BivyBorder.copy(alpha = 0.7f)
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, top),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }

            // Draw Playhead scrubber marker
            val playheadX = activeWidth.coerceIn(0f, width)
            drawCircle(
                color = if (isDragging) BivyCyan else BivyGoldLight,
                radius = if (isDragging) 6.dp.toPx() else 4.dp.toPx(),
                center = Offset(playheadX, height / 2f)
            )
        }
    }
}
