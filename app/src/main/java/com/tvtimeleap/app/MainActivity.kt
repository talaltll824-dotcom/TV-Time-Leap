package com.tvtimeleap.app

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var timeText: TextView
    private lateinit var timeline: SeekBar

    private var positionSeconds = 3600
    private val bufferSeconds = 3600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(35, 40, 35, 40)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "TV Time Leap"
            textSize = 30f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "1 Saatlik Canlı TV Zaman Kontrolü"
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            setPadding(0, 10, 0, 25)
        }

        statusText = TextView(this).apply {
            text = "TV bağlantısı bekleniyor"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            setPadding(0, 10, 0, 20)
        }

        timeText = TextView(this).apply {
            text = "CANLI"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
        }

        timeline = SeekBar(this).apply {
            max = bufferSeconds
            progress = bufferSeconds
        }

        val connectButton = Button(this).apply {
            text = "📺 TV'YE BAĞLAN"
        }

        val oneHourBackButton = Button(this).apply {
            text = "⏪ 1 SAAT GERİ"
        }

        val backButton = Button(this).apply {
            text = "↩ 10 SANİYE GERİ"
        }

        val playPauseButton = Button(this).apply {
            text = "▶ OYNAT / DURAKLAT"
        }

        val forwardButton = Button(this).apply {
            text = "10 SANİYE İLERİ ↪"
        }

        val continueButton = Button(this).apply {
            text = "▶ KALDIĞIN YERDEN DEVAM"
        }

        val liveButton = Button(this).apply {
            text = "🔴 CANLIYA DÖN"
        }

        val storageText = TextView(this).apply {
            text = "Geçici yayın deposu: Son 60 dakika"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            setPadding(0, 20, 0, 10)
        }

        connectButton.setOnClickListener {
            statusText.text = "TV bağlantısı hazırlanıyor..."
        }

        oneHourBackButton.setOnClickListener {
            positionSeconds = 0
            updateTimeline()
            statusText.text = "1 saat önceki noktaya gidildi"
        }

        backButton.setOnClickListener {
            positionSeconds = (positionSeconds - 10).coerceAtLeast(0)
            updateTimeline()
        }

        forwardButton.setOnClickListener {
            positionSeconds =
                (positionSeconds + 10).coerceAtMost(bufferSeconds)

            updateTimeline()
        }

        playPauseButton.setOnClickListener {
            statusText.text = "Oynat / Duraklat"
        }

        continueButton.setOnClickListener {
            statusText.text = "Kaldığın yerden devam ediliyor"
        }

        liveButton.setOnClickListener {
            positionSeconds = bufferSeconds
            updateTimeline()
            statusText.text = "Canlı yayına dönüldü"
        }

        timeline.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        positionSeconds = progress
                        updateTimeline()
                    }
                }

                override fun onStartTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }

                override fun onStopTrackingTouch(
                    seekBar: SeekBar?
                ) {
                }
            }
        )

        layout.addView(title)
        layout.addView(subtitle)

        layout.addView(statusText)

        layout.addView(timeText)
        layout.addView(timeline)

        layout.addView(connectButton)

        layout.addView(oneHourBackButton)
        layout.addView(backButton)

        layout.addView(playPauseButton)

        layout.addView(forwardButton)

        layout.addView(continueButton)

        layout.addView(liveButton)

        layout.addView(storageText)

        setContentView(layout)

        updateTimeline()
    }

    private fun updateTimeline() {

        timeline.progress = positionSeconds

        val secondsBehindLive =
            bufferSeconds - positionSeconds

        if (secondsBehindLive <= 0) {

            timeText.text = "🔴 CANLI"

        } else {

            val minutes =
                secondsBehindLive / 60

            val seconds =
                secondsBehindLive % 60

            timeText.text =
                String.format(
                    "-%02d:%02d",
                    minutes,
                    seconds
                )
        }
    }
}
