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
    private var commandServer: TvCommandServer? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("TV Time Leap")
            .setContentText("Arka planda çalışıyor")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)

        player = ExoPlayer.Builder(this).build()

        commandServer = TvCommandServer(
            port = 8765,
            onCommand = { command ->
                handleRemoteCommand(command)
            }
        )

        commandServer?.start()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START_STREAM -> {
                val url = intent.getStringExtra(
                    EXTRA_STREAM_URL
                )

                if (!url.isNullOrBlank()) {
                    startStream(url)
                }
            }

            ACTION_SEEK_BACK -> {
                val minutes = intent.getIntExtra(
                    EXTRA_MINUTES,
                    0
                )

                seekBack(minutes)
            }

            ACTION_LIVE -> {
                goLive()
            }

            ACTION_PLAY_PAUSE -> {
                togglePlayPause()
            }
        }

        return START_STICKY
    }

    private fun startStream(url: String) {
        val exoPlayer = player ?: return

        val mediaItem = MediaItem.fromUri(url)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun handleRemoteCommand(
        command: String
    ) {

        when {

            command == "LIVE" -> {
                goLive()
            }

            command == "PLAY_PAUSE" -> {
                togglePlayPause()
            }

            command.startsWith("BACK:") -> {
                val minutes = command
                    .substringAfter("BACK:")
                    .trim()
                    .toIntOrNull()

                if (minutes != null) {
                    seekBack(minutes)
                }
            }
        }
    }

    private fun seekBack(minutes: Int) {
        if (minutes <= 0) {
            return
        }

        val exoPlayer = player ?: return

        val amount =
            minutes.toLong() * 60_000L

        val target =
            (exoPlayer.currentPosition - amount)
                .coerceAtLeast(0L)

        exoPlayer.seekTo(target)
    }

    private fun goLive() {
        val exoPlayer = player ?: return

        exoPlayer.seekToDefaultPosition()
        exoPlayer.play()
    }

    private fun togglePlayPause() {
        val exoPlayer = player ?: return

        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
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

    override fun onBind(
        intent: Intent?
    ): IBinder? {
        return null
    }

    override fun onDestroy() {
        commandServer?.stop()
        commandServer = null

        player?.release()
        player = null

        super.onDestroy()
    }

    companion object {
        const val ACTION_START_STREAM =
            "com.tvtimeleap.app.START_STREAM"

        const val ACTION_SEEK_BACK =
            "com.tvtimeleap.app.SEEK_BACK"

        const val ACTION_LIVE =
            "com.tvtimeleap.app.LIVE"

        const val ACTION_PLAY_PAUSE =
            "com.tvtimeleap.app.PLAY_PAUSE"

        const val EXTRA_STREAM_URL =
            "stream_url"

        const val EXTRA_MINUTES =
            "minutes"

        private const val CHANNEL_ID =
            "tv_time_leap_buffer"
    }
}
