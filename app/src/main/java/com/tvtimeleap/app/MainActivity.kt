package com.tvtimeleap.app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.media.tv.TvView
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var tvView: TvView? = null
    private var timeShiftTester: TimeShiftTester? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isTv =
            (resources.configuration.uiMode and
                Configuration.UI_MODE_TYPE_MASK) ==
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
            setTextColor(Color.BLACK)
            setPadding(0, 30, 0, 30)
        }

        val startButton = Button(this).apply {
            text = "TV TIME LEAP BAŞLAT"
        }

        val tunerButton = Button(this).apply {
            text = "TUNER TEST"
        }

        val timeShiftButton = Button(this).apply {
            text = "TIMESHIFT TEST"
        }

        val hiddenTvView = TvView(this).apply {
            visibility = View.INVISIBLE
        }

        tvView = hiddenTvView

        startButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    BufferService::class.java
                )

            ContextCompat.startForegroundService(
                this,
                intent
            )

            status.text =
                "TV Time Leap arka plan servisi çalışıyor"
        }

        tunerButton.setOnClickListener {
            status.text = testTuner()
        }

        timeShiftButton.setOnClickListener {

            val currentTvView = tvView

            if (currentTvView == null) {
                status.text = "TV VIEW HATASI"
                return@setOnClickListener
            }

            timeShiftTester =
                TimeShiftTester(
                    context = this,
                    tvView = currentTvView,
                    onResult = { result ->

                        runOnUiThread {
                            status.text = result
                        }
                    }
                )

            timeShiftTester?.start()
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(startButton)
        layout.addView(tunerButton)
        layout.addView(timeShiftButton)
        layout.addView(hiddenTvView)

        setContentView(layout)
    }

    private fun testTuner(): String {

        return try {

            val manager =
                getSystemService(
                    Context.TV_INPUT_SERVICE
                ) as TvInputManager

            val tunerInputs =
                manager.tvInputList.filter {
                    it.type == TvInputInfo.TYPE_TUNER
                }

            if (tunerInputs.isEmpty()) {

                "TUNER BULUNAMADI"

            } else {

                val names =
                    tunerInputs.joinToString("\n") { input ->

                        try {

                            input.loadLabel(
                                this@MainActivity
                            ).toString()

                        } catch (_: Exception) {

                            input.id
                        }
                    }

                "TUNER BULUNDU\n$names"
            }

        } catch (e: Exception) {

            "TUNER TEST HATASI\n" +
                (e.message ?: "Bilinmeyen hata")
        }
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
            inputType =
                android.text.InputType.TYPE_CLASS_NUMBER
            setSingleLine(true)
        }

        val status = TextView(this).apply {
            text = "TV bağlantısı bekleniyor"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
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

            val ip =
                ipInput.text.toString().trim()

            val minutes =
                minuteInput.text
                    .toString()
                    .trim()
                    .toIntOrNull()

            when {

                ip.isBlank() -> {
                    status.text =
                        "TV IP adresini gir"
                }

                minutes == null ||
                    minutes <= 0 -> {

                    status.text =
                        "Geçerli dakika gir"
                }

                else -> {

                    status.text =
                        "Gönderiliyor..."

                    client.seekBack(
                        ip,
                        minutes
                    ) { success ->

                        runOnUiThread {

                            status.text =
                                if (success) {
                                    "Geri sarma komutu gönderildi"
                                } else {
                                    "TV'ye bağlanılamadı"
                                }
                        }
                    }
                }
            }
        }

        playButton.setOnClickListener {

            val ip =
                ipInput.text.toString().trim()

            if (ip.isBlank()) {

                status.text =
                    "TV IP adresini gir"

            } else {

                status.text =
                    "Gönderiliyor..."

                client.playPause(
                    ip
                ) { success ->

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

            val ip =
                ipInput.text.toString().trim()

            if (ip.isBlank()) {

                status.text =
                    "TV IP adresini gir"

            } else {

                status.text =
                    "Gönderiliyor..."

                client.goLive(
                    ip
                ) { success ->

                    runOnUiThread {

                        status.text =
                            if (success) {
                                "Canlıya dön komutu gönderildi"
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

    override fun onDestroy() {

        try {
            tvView?.reset()
        } catch (_: Exception) {
        }

        tvView = null
        timeShiftTester = null

        super.onDestroy()
    }
}
