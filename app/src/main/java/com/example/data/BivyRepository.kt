package com.example.data

import android.content.Context
import com.example.model.AudioDeviceType
import com.example.model.AudioPreset
import com.example.model.AudioProfile
import com.example.model.ListeningHistory
import com.example.model.Playlist
import com.example.model.PlaylistSongCrossRef
import com.example.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class BivyRepository(private val context: Context) {
    private val db = BivyDatabase.getInstance(context)
    private val songDao = db.songDao()
    private val playlistDao = db.playlistDao()
    private val presetDao = db.audioPresetDao()
    private val profileDao = db.audioProfileDao()
    private val historyDao = db.listeningHistoryDao()
    private val scanner = MediaScanner(context)

    val allSongs: Flow<List<Song>> = songDao.getAllSongs()
    val favoriteSongs: Flow<List<Song>> = songDao.getFavoriteSongs()
    val recentlyPlayed: Flow<List<Song>> = songDao.getRecentlyPlayedSongs()
    val mostPlayed: Flow<List<Song>> = songDao.getMostPlayedSongs()
    val losslessSongs: Flow<List<Song>> = songDao.getLosslessSongs()
    val longSongs: Flow<List<Song>> = songDao.getLongSongs()
    val forgottenFavorites: Flow<List<Song>> = songDao.getForgottenFavorites(System.currentTimeMillis() - 30L * 24 * 3600 * 1000)

    val allPresets: Flow<List<AudioPreset>> = presetDao.getAllPresets()
    val allProfiles: Flow<List<AudioProfile>> = profileDao.getAllProfiles()
    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    val totalListeningTimeMs: Flow<Long?> = historyDao.getTotalListeningTimeMs()
    val recentHistory: Flow<List<ListeningHistory>> = historyDao.getRecentHistory()

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val count = songDao.getSongCount()
        if (count == 0) {
            songDao.insertSongs(DefaultData.DEMO_STUDIO_TRACKS)
            presetDao.insertPresets(DefaultData.SYSTEM_PRESETS)
            profileDao.insertProfiles(DefaultData.DEFAULT_PROFILES)
        }
    }

    suspend fun scanMedia(): Int = withContext(Dispatchers.IO) {
        val localSongs = scanner.scanLocalAudio()
        if (localSongs.isNotEmpty()) {
            songDao.insertSongs(localSongs)
        }
        localSongs.size
    }

    suspend fun toggleFavorite(song: Song) = withContext(Dispatchers.IO) {
        songDao.setFavorite(song.id, !song.isFavorite)
    }

    suspend fun recordPlayback(song: Song, durationMs: Long) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        songDao.markPlayed(song.id, now)
        historyDao.insertHistory(
            ListeningHistory(
                songId = song.id,
                songTitle = song.title,
                artist = song.artist,
                timestamp = now,
                durationListenedMs = durationMs
            )
        )
    }

    suspend fun savePreset(preset: AudioPreset) = withContext(Dispatchers.IO) {
        presetDao.insertPreset(preset)
    }

    suspend fun deletePreset(id: String) = withContext(Dispatchers.IO) {
        presetDao.deleteCustomPreset(id)
    }

    suspend fun saveProfile(profile: AudioProfile) = withContext(Dispatchers.IO) {
        profileDao.insertProfile(profile)
    }

    suspend fun getProfileForDevice(deviceType: AudioDeviceType): AudioProfile? = withContext(Dispatchers.IO) {
        profileDao.getProfileForDevice(deviceType.name)
    }

    suspend fun createPlaylist(name: String, description: String, songs: List<Song>, isVibeMix: Boolean = false, vibeTag: String? = null): Long = withContext(Dispatchers.IO) {
        val playlist = Playlist(
            name = name,
            description = description,
            isVibeMix = isVibeMix,
            vibeTag = vibeTag
        )
        val playlistId = playlistDao.insertPlaylist(playlist)
        val refs = songs.mapIndexed { index, song ->
            PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = song.id,
                orderIndex = index
            )
        }
        playlistDao.insertPlaylistSongs(refs)
        playlistId
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylist(playlistId)
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsForPlaylist(playlistId)
    }

    suspend fun generateVibeMix(mood: String, activity: String, energy: String): Pair<Playlist, List<Song>> = withContext(Dispatchers.IO) {
        val availableSongs = allSongs.firstOrNull() ?: emptyList()
        val pool = if (availableSongs.isEmpty()) DefaultData.DEMO_STUDIO_TRACKS else availableSongs

        // Local algorithmic sorting/filtering according to mood & energy without external network calls
        val scoredSongs = pool.map { song ->
            var score = 0
            val combinedText = "${song.title} ${song.genre} ${song.album} ${song.artist}".lowercase()

            when (mood.lowercase()) {
                "relax" -> if (combinedText.contains("ambient") || combinedText.contains("rain") || combinedText.contains("acoustic") || combinedText.contains("classical") || song.durationMs > 200000) score += 3
                "energetic" -> if (combinedText.contains("pulse") || combinedText.contains("dance") || combinedText.contains("rock") || combinedText.contains("electronic")) score += 3
                "focus" -> if (combinedText.contains("horizon") || combinedText.contains("drift") || combinedText.contains("kinetic") || song.isLossless) score += 3
                "melancholic" -> if (combinedText.contains("strings") || combinedText.contains("rain") || combinedText.contains("soul")) score += 3
                "dark" -> if (combinedText.contains("subterranean") || combinedText.contains("monolith") || combinedText.contains("night")) score += 3
                "euphoric" -> if (combinedText.contains("orbit") || combinedText.contains("solar") || song.playCount > 10) score += 3
            }

            when (energy.lowercase()) {
                "low" -> if (song.durationMs >= 240000L || song.genre.contains("Classical") || song.genre.contains("Ambient")) score += 2
                "high" -> if (song.durationMs < 220000L || song.genre.contains("Electronic") || song.genre.contains("Rock")) score += 2
                else -> score += 1
            }

            when (activity.lowercase()) {
                "workout" -> if (song.codec.contains("PCM") || song.bitrateKbps >= 900) score += 2
                "study" -> if (song.isLossless) score += 2
                "night drive" -> if (song.genre.contains("Neo") || song.genre.contains("Soul") || song.genre.contains("Synth")) score += 2
                "travel" -> if (song.isFavorite) score += 2
                "coding" -> if (song.genre.contains("Electronic") || song.genre.contains("Atmospheric")) score += 2
                else -> score += 1
            }

            song to score
        }

        val sorted = scoredSongs.sortedByDescending { it.second }.map { it.first }
        val vibeMixSelection = if (sorted.size > 8) sorted.take(8) else sorted

        val vibeTitle = "$mood • $activity"
        val vibeDesc = "Engineered for $energy Energy with local acoustic profile."
        val playlist = Playlist(
            name = vibeTitle,
            description = vibeDesc,
            isVibeMix = true,
            vibeTag = "$mood / $activity / $energy"
        )
        playlist to vibeMixSelection
    }
}
