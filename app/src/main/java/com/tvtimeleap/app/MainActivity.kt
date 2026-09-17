package com.tvtimeleap.app

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isTv =
            (resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) ==
                Configuration.UI_MODE_TYPE_TELEVISION

        if (isTv) {
            openTvScreen()
        } else {
            openPhoneScreen()
        }
    }

    private fun baseLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
            setBackgroundColor(Color.WHITE)
        }
    }

    private fun openTvScreen() {

        val layout = baseLayout()

        val title = TextView(this).apply {
            text = "TV Time Leap"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
        }

        val status = TextView(this).apply {
            text = "Hazır"
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 30)
        }

        val startButton = Button(this).apply {
            text = "TV TIME LEAP BAŞLAT"
        }

        startButton.setOnClickListener {
            val intent = Intent(
                this,
                BufferService::class.java
            )

            ContextCompat.startForegroundService(
                this,
                intent
            )

            status.text = "Telefon bağlantısı bekleniyor"
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(startButton)

        setContentView(layout)
    }

    private fun openPhoneScreen() {

        val layout = baseLayout()

        val title = TextView(this).apply {
            text = "TV Time Leap"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
        }

        val ipInput = EditText(this).apply {
            hint = "TV IP adresi"
            setSingleLine(true)
        }

        val minuteInput = EditText(this).apply {
            hint = "Kaç dakika geri?"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setSingleLine(true)
        }

        val status = TextView(this).apply {
            text = "TV bağlantısı bekleniyor"
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 20)
        }

        val backButton = Button(this).apply {
            text = "GERİ SAR"
        }

        val playButton = Button(this).apply {
            text = "OYNAT / DURAKLAT"
        }

        val liveButton = Button(this).apply {
            text = "CANLIYA DÖN"
        }

        val client = TvCommandClient()

        backButton.setOnClickListener {

            val ip = ipInput.text.toString().trim()
            val minutes =
                minuteInput.text.toString().toIntOrNull()

            if (ip.isBlank()) {
                status.text = "TV IP adresini gir"
            } else if (minutes == null || minutes <= 0) {
                status.text = "Geçerli dakika gir"
            } else {
                status.text = "Gönderiliyor..."

                client.seekBack(ip, minutes) { success ->
                    runOnUiThread {
                        status.text =
                            if (success) {
                                "$minutes dakika geri sarıldı"
                            } else {
                                "TV'ye bağlanılamadı"
                            }
                    }
                }
            }
        }

        playButton.setOnClickListener {

            val ip = ipInput.text.toString().trim()

            if (ip.isBlank()) {
                status.text = "TV IP adresini gir"
            } else {
                client.playPause(ip) { success ->
                    runOnUiThread {
                        status.text =
                            if (success) {
                                "Oynat / Duraklat gönderildi"
                            } else {
                                "TV'ye bağlanılamadı"
                            }
                    }
                }
            }
        }

        liveButton.setOnClickListener {

            val ip = ipInput.text.toString().trim()

            if (ip.isBlank()) {
                status.text = "TV IP adresini gir"
            } else {
                client.goLive(ip) { success ->
                    runOnUiThread {
                        status.text =
                            if (success) {
                                "Canlı yayına dönüldü"
                            } else {
                                "TV'ye bağlanılamadı"
                            }
                    }
                }
            }
        }

        layout.addView(title)
        layout.addView(ipInput)
        layout.addView(minuteInput)
        layout.addView(backButton)
        layout.addView(playButton)
        layout.addView(liveButton)
        layout.addView(status)

        setContentView(layout)
    }
}
