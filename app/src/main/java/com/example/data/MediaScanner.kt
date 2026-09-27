package com.example.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaScanner(private val context: Context) {

    suspend fun scanLocalAudio(): List<Song> = withContext(Dispatchers.IO) {
        val songList = mutableListOf<Song>()
        val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 10000"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        try {
            val cursor = context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val yearCol = c.getColumnIndex(MediaStore.Audio.Media.YEAR)
                val trackCol = c.getColumnIndex(MediaStore.Audio.Media.TRACK)
                val mimeCol = c.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)
                val albumIdCol = c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val dateAddedCol = c.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val title = c.getString(titleCol) ?: "Untitled Track"
                    val artist = c.getString(artistCol) ?: "Unknown Artist"
                    val album = c.getString(albumCol) ?: "Unknown Album"
                    val duration = c.getLong(durationCol)
                    val dataPath = c.getString(dataCol) ?: ""
                    val year = if (yearCol != -1) c.getInt(yearCol) else 2026
                    val track = if (trackCol != -1) c.getInt(trackCol) else 1
                    val mime = if (mimeCol != -1) c.getString(mimeCol) ?: "audio/mpeg" else "audio/mpeg"
                    val dateAdded = if (dateAddedCol != -1) c.getLong(dateAddedCol) * 1000L else System.currentTimeMillis()

                    val albumId = if (albumIdCol != -1) c.getLong(albumIdCol) else -1L
                    val artworkUri = if (albumId != -1L) {
                        ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        ).toString()
                    } else null

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val isLossless = mime.contains("flac", ignoreCase = true) ||
                            mime.contains("wav", ignoreCase = true) ||
                            mime.contains("alac", ignoreCase = true) ||
                            dataPath.endsWith(".flac", ignoreCase = true) ||
                            dataPath.endsWith(".wav", ignoreCase = true)

                    val codecName = when {
                        mime.contains("flac") || dataPath.endsWith(".flac") -> "FLAC Lossless"
                        mime.contains("wav") || dataPath.endsWith(".wav") -> "WAV PCM"
                        mime.contains("opus") || dataPath.endsWith(".opus") -> "OPUS"
                        mime.contains("ogg") || dataPath.endsWith(".ogg") -> "OGG Vorbis"
                        mime.contains("mp4") || mime.contains("m4a") || dataPath.endsWith(".m4a") -> "AAC/M4A"
                        else -> "MP3"
                    }

                    songList.add(
                        Song(
                            id = "local_$id",
                            title = title,
                            artist = if (artist == "<unknown>") "Unknown Artist" else artist,
                            album = if (album == "<unknown>") "Unknown Album" else album,
                            durationMs = duration,
                            dataUri = contentUri,
                            artworkUri = artworkUri,
                            genre = "Local Audio",
                            year = if (year > 1900) year else 2026,
                            trackNumber = track,
                            bitrateKbps = if (isLossless) 1411 else 320,
                            sampleRateHz = 48000,
                            codec = codecName,
                            isLossless = isLossless,
                            dateAdded = dateAdded
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        songList
    }
}
