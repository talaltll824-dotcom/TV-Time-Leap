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
                .setContentText("Yayın sistemi çalışıyor")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build()

        startForeground(
            NOTIFICATION_ID,
            notification
        )

        player = ExoPlayer.Builder(this).build()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START_STREAM -> {
                val url =
                    intent.getStringExtra(
                        EXTRA_STREAM_URL
                    )

                if (!url.isNullOrBlank()) {
                    startStream(url)
                }
            }

            ACTION_SEEK_BACK -> {
                val minutes =
                    intent.getIntExtra(
                        EXTRA_MINUTES,
                        0
                    )

                seekBack(minutes)
            }

            ACTION_PLAY_PAUSE -> {
                togglePlayPause()
            }

            ACTION_LIVE -> {
                goLive()
            }
        }

        return START_STICKY
    }

    private fun startStream(url: String) {

        val mediaItem =
            MediaItem.fromUri(url)

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    private fun seekBack(minutes: Int) {

        if (minutes <= 0) {
            return
        }

        val current =
            player?.currentPosition ?: return

        val backMilliseconds =
            minutes.toLong() * 60_000L

        val target =
            (current - backMilliseconds)
                .coerceAtLeast(0L)

        player?.seekTo(target)
    }

    private fun togglePlayPause() {

        val exoPlayer =
            player ?: return

        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    private fun goLive() {

        val exoPlayer =
            player ?: return

        exoPlayer.seekToDefaultPosition()
        exoPlayer.play()
    }

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
                    "TV Time Leap",
                    NotificationManager.IMPORTANCE_LOW
                )

            manager.createNotificationChannel(
                channel
            )
        }
    }

    override fun onDestroy() {

        player?.release()
        player = null

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {
        return null
    }

    companion object {

        const val ACTION_START_STREAM =
            "com.tvtimeleap.app.START_STREAM"

        const val ACTION_SEEK_BACK =
            "com.tvtimeleap.app.SEEK_BACK"

        const val ACTION_PLAY_PAUSE =
            "com.tvtimeleap.app.PLAY_PAUSE"

        const val ACTION_LIVE =
            "com.tvtimeleap.app.LIVE"

        const val EXTRA_STREAM_URL =
            "stream_url"

        const val EXTRA_MINUTES =
            "minutes"

        private const val CHANNEL_ID =
            "tv_time_leap_channel"

        private const val NOTIFICATION_ID =
            1001
    }
}
