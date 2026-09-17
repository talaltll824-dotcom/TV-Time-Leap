package com.tvtimeleap.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class BufferService : Service() {

    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TV Time Leap")
                .setContentText("Yayın arka planda çalışıyor")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build()

        startForeground(1001, notification)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val streamUrl =
            intent?.getStringExtra(EXTRA_STREAM_URL)

        if (!streamUrl.isNullOrBlank()) {

            if (player == null) {
                player = ExoPlayer.Builder(this).build()
            }

            player?.apply {

                setMediaItem(
                    MediaItem.fromUri(streamUrl)
                )

                prepare()

                playWhenReady = true

                // Arka planda ikinci ses çıkmasın
                volume = 0f
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {

        player?.release()

        player = null

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val manager =
                getSystemService(
                    NotificationManager::class.java
                )

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "TV Time Leap Buffer",
                    NotificationManager.IMPORTANCE_LOW
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    companion object {

        const val EXTRA_STREAM_URL =
            "stream_url"

        private const val CHANNEL_ID =
            "tv_time_leap_buffer"
    }
}
