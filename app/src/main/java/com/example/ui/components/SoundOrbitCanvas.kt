package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.ui.theme.BivyBackground
import com.example.ui.theme.BivyBorder
import com.example.ui.theme.BivyCyan
import com.example.ui.theme.BivyGold
import com.example.ui.theme.BivyGoldLight
import com.example.ui.theme.BivySurface
import com.example.ui.theme.BivySurfaceElevated
import com.example.ui.theme.BivyViolet
import com.example.viewmodel.OrbitRing
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SoundOrbitCanvas(
    currentSong: Song?,
    isPlaying: Boolean,
    onCenterClick: () -> Unit,
    onNodeClick: (OrbitRing) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit_transition")

    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) 32000 else 64000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rotation"
    )

    val centerPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) 900 else 2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "center_pulse"
    )

    val nodes = remember {
        listOf(
            Triple(OrbitRing.RECENT, Icons.Default.History, BivyCyan),
            Triple(OrbitRing.FAVORITES, Icons.Default.Favorite, Color(0xFFFF4081)),
            Triple(OrbitRing.ALBUMS, Icons.Default.Album, BivyGold),
            Triple(OrbitRing.ARTISTS, Icons.Default.Person, Color(0xFF64B5F6)),
            Triple(OrbitRing.PLAYLISTS, Icons.Default.LibraryMusic, Color(0xFF81C784)),
            Triple(OrbitRing.GENRES, Icons.Default.Category, Color(0xFFFFB74D)),
            Triple(OrbitRing.MOODS, Icons.Default.AutoAwesome, BivyViolet),
            Triple(OrbitRing.STUDIO_FX, Icons.Default.GraphicEq, BivyCyan)
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("sound_orbit_container"),
        contentAlignment = Alignment.Center
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val center = Offset(widthPx / 2f, heightPx / 2f)
        val maxOrbitRadius = (minOf(widthPx, heightPx) / 2f) * 0.78f
        val innerOrbitRadius = maxOrbitRadius * 0.62f

        // Draw Orbit Tracks & Ambient Starfield
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        BivyGold.copy(alpha = if (isPlaying) 0.12f else 0.05f),
                        BivyCyan.copy(alpha = if (isPlaying) 0.08f else 0.03f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxOrbitRadius * 1.3f
                ),
                radius = maxOrbitRadius * 1.3f,
                center = center
            )

            // Inner Orbit Ring
            drawCircle(
                color = BivyBorder.copy(alpha = 0.5f),
                radius = innerOrbitRadius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Outer Orbit Ring
            drawCircle(
                color = BivyBorder.copy(alpha = 0.4f),
                radius = maxOrbitRadius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Harmonic concentric resonance waves when playing
            if (isPlaying) {
                drawCircle(
                    color = BivyGold.copy(alpha = 0.18f * centerPulse),
                    radius = innerOrbitRadius * 1.12f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = BivyCyan.copy(alpha = 0.15f * centerPulse),
                    radius = maxOrbitRadius * 1.08f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Render Orbit Nodes (Placed along the 2 rings)
        nodes.forEachIndexed { index, (ring, icon, accentColor) ->
            val isOuter = index % 2 == 0
            val radius = if (isOuter) maxOrbitRadius else innerOrbitRadius
            val baseAngle = (index * (360f / nodes.size)) + orbitRotation
            val rad = Math.toRadians(baseAngle.toDouble())
            val x = (center.x + radius * cos(rad)).toInt()
            val y = (center.y + radius * sin(rad)).toInt()

            Box(
                modifier = Modifier
                    .offset { IntOffset(x - 30.dp.roundToPx(), y - 30.dp.roundToPx()) }
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BivySurfaceElevated.copy(alpha = 0.88f))
                    .border(1.5.dp, accentColor.copy(alpha = 0.65f), CircleShape)
                    .shadow(8.dp, CircleShape)
                    .clickable { onNodeClick(ring) }
                    .testTag("orbit_node_${ring.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = ring.label,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Central Animated Vinyl / Album Disc
        val centerSize = (minOf(maxWidth, maxHeight) * 0.38f).coerceIn(130.dp, 190.dp)

        Box(
            modifier = Modifier
                .size(centerSize * centerPulse)
                .shadow(24.dp, CircleShape, spotColor = BivyGold.copy(alpha = 0.4f))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF1E2638), Color(0xFF10141E), Color(0xFF090C12))
                    )
                )
                .border(2.5.dp, BivyGold.copy(alpha = 0.85f), CircleShape)
                .clickable { onCenterClick() }
                .testTag("sound_orbit_center"),
            contentAlignment = Alignment.Center
        ) {
            // Concentric vinyl grooves
            Canvas(modifier = Modifier.fillMaxSize().rotate(if (isPlaying) orbitRotation else 0f)) {
                val discCenter = Offset(size.width / 2f, size.height / 2f)
                val discR = size.width / 2f
                drawCircle(color = Color(0x22FFFFFF), radius = discR * 0.85f, center = discCenter, style = Stroke(1.dp.toPx()))
                drawCircle(color = Color(0x18FFFFFF), radius = discR * 0.72f, center = discCenter, style = Stroke(1.dp.toPx()))
                drawCircle(color = Color(0x12FFFFFF), radius = discR * 0.58f, center = discCenter, style = Stroke(1.dp.toPx()))
            }

            // Central core label
            Box(
                modifier = Modifier
                    .size(centerSize * 0.46f)
                    .clip(CircleShape)
                    .background(BivySurface)
                    .border(1.5.dp, BivyGoldLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = "Center Audio Studio",
                    tint = if (isPlaying) BivyGold else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
