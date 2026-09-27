package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AudioPreset
import com.example.model.AudioProfile
import com.example.model.ListeningHistory
import com.example.model.Playlist
import com.example.model.PlaylistSongCrossRef
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Song::class,
        Playlist::class,
        PlaylistSongCrossRef::class,
        AudioPreset::class,
        AudioProfile::class,
        ListeningHistory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BivyDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun audioPresetDao(): AudioPresetDao
    abstract fun audioProfileDao(): AudioProfileDao
    abstract fun listeningHistoryDao(): ListeningHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: BivyDatabase? = null

        fun getInstance(context: Context): BivyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BivyDatabase::class.java,
                    "bivy_audio_studio.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default presets and seed tracks in background
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                database.audioPresetDao().insertPresets(DefaultData.SYSTEM_PRESETS)
                                database.audioProfileDao().insertProfiles(DefaultData.DEFAULT_PROFILES)
                                database.songDao().insertSongs(DefaultData.DEMO_STUDIO_TRACKS)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
