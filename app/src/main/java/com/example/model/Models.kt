package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val dataUri: String,
    val artworkUri: String? = null,
    val genre: String = "Studio Master",
    val year: Int = 2026,
    val trackNumber: Int = 1,
    val bitrateKbps: Int = 320,
    val sampleRateHz: Int = 48000,
    val codec: String = "FLAC",
    val isLossless: Boolean = true,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val lyrics: String? = null
)

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isVibeMix: Boolean = false,
    val vibeTag: String? = null
)

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"]
)
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: String,
    val orderIndex: Int = 0
)

@Entity(tableName = "audio_presets")
data class AudioPreset(
    @PrimaryKey
    val id: String,
    val name: String,
    val isSystem: Boolean = false,
    val bandLevelsString: String = "0,0,0,0,0,0,0,0,0,0", // 10 bands in dB (-12 to +12)
    val preampGainMb: Int = 0,
    val bassBoostStrength: Int = 0,        // 0..1000
    val virtualizerStrength: Int = 0,      // 0..1000
    val reverbPreset: String = "NONE",     // NONE, SMALL_ROOM, MEDIUM_ROOM, LARGE_ROOM, MEDIUM_HALL, LARGE_HALL, PLATE
    val trebleGainDb: Int = 0,             // -12..+12
    val loudnessGainMb: Int = 0,           // 0..1000
    val speedRatio: Float = 1.0f,          // 0.5..2.0
    val pitchRatio: Float = 1.0f,          // 0.5..2.0
    val stereoWidth: Int = 50,             // 0..100
    val stereoBalance: Float = 0.0f        // -1.0 (L) to +1.0 (R)
) {
    fun getBandLevels(): List<Int> {
        return bandLevelsString.split(",").mapNotNull { it.trim().toIntOrNull() }.ifEmpty {
            List(10) { 0 }
        }
    }
}

enum class AudioDeviceType(val displayName: String) {
    EARBUDS("Studio In-Ear / Earbuds"),
    HEADPHONES("Over-Ear Reference Monitors"),
    BLUETOOTH_SPEAKER("Wireless Studio Speaker"),
    CAR("Car Studio Audio"),
    PHONE_SPEAKER("Acoustic Phone Speaker"),
    CUSTOM("Custom Device Rig")
}

@Entity(tableName = "audio_profiles")
data class AudioProfile(
    @PrimaryKey
    val deviceType: String, // String representation of AudioDeviceType
    val presetId: String,
    val customName: String = "",
    val autoActivate: Boolean = true
)

@Entity(tableName = "listening_history")
data class ListeningHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val songId: String,
    val songTitle: String,
    val artist: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationListenedMs: Long = 0L
)

enum class PlaybackMode {
    REPEAT_ALL,
    REPEAT_ONE,
    SHUFFLE
}
