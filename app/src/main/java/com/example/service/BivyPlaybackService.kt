package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.Song

class BivyPlaybackService : Service() {

    private val binder = LocalBinder()
    private val CHANNEL_ID = "bivy_playback_channel"
    private val NOTIFICATION_ID = 1001
    private var isForeground = false

    inner class LocalBinder : Binder() {
        fun getService(): BivyPlaybackService = this@BivyPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bivy Audio Studio Playback"
            val descriptionText = "Playback notifications for Bivy Audio Studio"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(song: Song?, isPlaying: Boolean) {
        if (song == null) {
            if (isForeground) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                isForeground = false
            }
            return
        }

        try {
            val openAppIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(song.title)
                .setContentText("${song.artist} • ${song.album}")
                .setSubText("BIVY STUDIO • ${song.codec}")
                .setSmallIcon(R.drawable.ic_bivy_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(isPlaying)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            isForeground = true
        } catch (e: Exception) {
            Log.e("BivyPlaybackService", "Error posting notification: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isForeground) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            isForeground = false
        }
    }
}
