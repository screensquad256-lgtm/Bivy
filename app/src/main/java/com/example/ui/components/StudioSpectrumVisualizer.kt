package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.audio.VisualizerSnapshot
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyEmerald
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivyViolet

enum class VisualizerMode {
    SPECTRUM,
    WAVEFORM,
    AURA_PULSE
}

@Composable
fun StudioSpectrumVisualizer(
    snapshot: VisualizerSnapshot,
    isPlaying: Boolean,
    mode: VisualizerMode = VisualizerMode.SPECTRUM,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .testTag("studio_spectrum_visualizer")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (mode) {
                VisualizerMode.SPECTRUM -> {
                    val bands = snapshot.spectrumBands
                    val barCount = bands.size
                    val spacing = 4.dp.toPx()
                    val barWidth = (w - (barCount - 1) * spacing) / barCount

                    for (i in 0 until barCount) {
                        val mag = if (isPlaying) bands[i] else 0.08f
                        val barH = (h * mag).coerceIn(4.dp.toPx(), h)
                        val x = i * (barWidth + spacing)
                        val top = h - barH

                        val brush = Brush.verticalGradient(
                            colors = listOf(
                                BivyGold,
                                BivyCyan,
                                BivyViolet
                            ),
                            startY = top,
                            endY = h
                        )

                        drawRoundRect(
                            brush = brush,
                            topLeft = Offset(x, top),
                            size = Size(barWidth, barH),
                            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                        )
                    }
                }
                VisualizerMode.WAVEFORM -> {
                    val points = snapshot.waveformPoints
                    val step = w / (points.size - 1)
                    val midY = h / 2f

                    for (i in 0 until points.size - 1) {
                        val y1 = midY + (points[i] - 0.5f) * h * 0.85f
                        val y2 = midY + (points[i + 1] - 0.5f) * h * 0.85f

                        drawLine(
                            color = if (isPlaying) BivyCyan else Color(0xFF334155),
                            start = Offset(i * step, y1),
                            end = Offset((i + 1) * step, y2),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                VisualizerMode.AURA_PULSE -> {
                    val peak = if (isPlaying) snapshot.peakLevel else 0.15f
                    val center = Offset(w / 2f, h / 2f)
                    val radius = (h / 2f) * peak * 1.2f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                BivyEmerald.copy(alpha = 0.4f * peak),
                                BivyCyan.copy(alpha = 0.2f * peak),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }
            }
        }
    }
}
