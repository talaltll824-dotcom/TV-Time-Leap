package com.tvtimeleap.app

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var rewindButton: Button
    private lateinit var playPauseButton: Button
    private lateinit var forwardButton: Button
    private lateinit var liveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 50, 40, 50)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "TV Time Leap"
            textSize = 32f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        val description = TextView(this).apply {
            text = "Android TV Kontrol"
            textSize = 18f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 15, 0, 30)
        }

        statusText = TextView(this).apply {
            text = "TV bağlantısı bekleniyor"
            textSize = 18f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 30)
        }

        val connectButton = Button(this).apply {
            text = "TV'YE BAĞLAN"
        }

        rewindButton = Button(this).apply {
            text = "⏪ 10 SANİYE GERİ"
            isEnabled = false
        }

        playPauseButton = Button(this).apply {
            text = "▶ OYNAT / DURAKLAT"
            isEnabled = false
        }

        forwardButton = Button(this).apply {
            text = "10 SANİYE İLERİ ⏩"
            isEnabled = false
        }

        liveButton = Button(this).apply {
            text = "📺 CANLIYA DÖN"
            isEnabled = false
        }

        connectButton.setOnClickListener {
            statusText.text = "TV bağlantısı hazırlanıyor..."

            rewindButton.isEnabled = true
            playPauseButton.isEnabled = true
            forwardButton.isEnabled = true
            liveButton.isEnabled = true
        }

        rewindButton.setOnClickListener {
            statusText.text = "10 saniye geri sar"
        }

        playPauseButton.setOnClickListener {
            statusText.text = "Oynat / Duraklat"
        }

        forwardButton.setOnClickListener {
            statusText.text = "10 saniye ileri sar"
        }

        liveButton.setOnClickListener {
            statusText.text = "Canlı yayına dön"
        }

        mainLayout.addView(title)
        mainLayout.addView(description)
        mainLayout.addView(statusText)
        mainLayout.addView(connectButton)
        mainLayout.addView(rewindButton)
        mainLayout.addView(playPauseButton)
        mainLayout.addView(forwardButton)
        mainLayout.addView(liveButton)

        setContentView(mainLayout)
    }
}
