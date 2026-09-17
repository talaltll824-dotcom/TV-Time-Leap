package com.tvtimeleap.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializePlayer()
    }

    private fun initializePlayer() {
        val playerView = findViewById<PlayerView>(R.id.playerView)

        player = ExoPlayer.Builder(this).build().also {
            playerView.player = it

            // Buraya daha sonra canlı TV yayın adresini bağlayacağız.
            val mediaItem = MediaItem.fromUri(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            )

            it.setMediaItem(mediaItem)
            it.prepare()
            it.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
