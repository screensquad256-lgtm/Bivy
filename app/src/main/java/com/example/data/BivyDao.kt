package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.model.AudioPreset
import com.example.model.AudioProfile
import com.example.model.ListeningHistory
import com.example.model.Playlist
import com.example.model.PlaylistSongCrossRef
import com.example.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY dateAdded DESC")
    fun getAllSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE lastPlayedTimestamp > 0 ORDER BY lastPlayedTimestamp DESC LIMIT 30")
    fun getRecentlyPlayedSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY playCount DESC LIMIT 20")
    fun getMostPlayedSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE isFavorite = 1 AND lastPlayedTimestamp < :cutoffTimestamp ORDER BY lastPlayedTimestamp ASC")
    fun getForgottenFavorites(cutoffTimestamp: Long): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE isLossless = 1 ORDER BY title ASC")
    fun getLosslessSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE durationMs >= 300000 ORDER BY durationMs DESC")
    fun getLongSongs(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    suspend fun getSongById(id: String): Song?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<Song>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Update
    suspend fun updateSong(song: Song)

    @Query("UPDATE songs SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE songs SET playCount = playCount + 1, lastPlayedTimestamp = :timestamp WHERE id = :id")
    suspend fun markPlayed(id: String, timestamp: Long)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: String)

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongCount(): Int
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :id LIMIT 1")
    suspend fun getPlaylistById(id: Long): Playlist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSongs(refs: List<PlaylistSongCrossRef>)

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN playlist_songs ps ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.orderIndex ASC
    """)
    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>>

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)
}

@Dao
interface AudioPresetDao {
    @Query("SELECT * FROM audio_presets ORDER BY isSystem DESC, name ASC")
    fun getAllPresets(): Flow<List<AudioPreset>>

    @Query("SELECT * FROM audio_presets WHERE id = :id LIMIT 1")
    suspend fun getPresetById(id: String): AudioPreset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: AudioPreset)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPresets(presets: List<AudioPreset>)

    @Query("DELETE FROM audio_presets WHERE id = :id AND isSystem = 0")
    suspend fun deleteCustomPreset(id: String)
}

@Dao
interface AudioProfileDao {
    @Query("SELECT * FROM audio_profiles")
    fun getAllProfiles(): Flow<List<AudioProfile>>

    @Query("SELECT * FROM audio_profiles WHERE deviceType = :type LIMIT 1")
    suspend fun getProfileForDevice(type: String): AudioProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: AudioProfile)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfiles(profiles: List<AudioProfile>)
}

@Dao
interface ListeningHistoryDao {
    @Query("SELECT * FROM listening_history ORDER BY timestamp DESC LIMIT 100")
    fun getRecentHistory(): Flow<List<ListeningHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ListeningHistory)

    @Query("SELECT SUM(durationListenedMs) FROM listening_history")
    fun getTotalListeningTimeMs(): Flow<Long?>
}
