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

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TV Time Leap")
                .setContentText("TV Time Leap arka planda çalışıyor")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .build()

        startForeground(1001, notification)

        player = ExoPlayer.Builder(this).build()

        commandServer = TvCommandServer(8765) { command ->
            handleRemoteCommand(command)
        }

        commandServer?.start()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START_STREAM -> {
                val streamUrl =
                    intent.getStringExtra(EXTRA_STREAM_URL)

                if (!streamUrl.isNullOrBlank()) {
                    startStream(streamUrl)
                }
            }

            ACTION_SEEK_BACK -> {
                val minutes =
                    intent.getIntExtra(EXTRA_MINUTES, 0)

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

    private fun startStream(streamUrl: String) {

        val mediaItem =
            MediaItem.fromUri(streamUrl)

        player?.apply {
            setMediaItem(mediaItem)
            prepare
