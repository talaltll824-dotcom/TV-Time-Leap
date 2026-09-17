package com.tvtimeleap.app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private val client = TvCommandClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isTelevision()) {
            showTvScreen()
        } else {
            showPhoneScreen()
        }
    }

    private fun isTelevision(): Boolean {
        val mode =
            resources.configuration.uiMode and
                Configuration.UI_MODE_TYPE_MASK

        return mode == Configuration.UI_MODE_TYPE_TELEVISION
    }

    private fun showTvScreen() {

        val layout = createMainLayout()

        val title = createTitle(
            "TV Time Leap - TV"
        )

        statusText = createStatus(
            "Telefon bağlantısı bekleniyor"
        )

        val startButton = Button(this).apply {
            text = "TV TIME LEAP BAŞLAT"
        }

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

            statusText.text =
                "Hazır - Telefon komutları bekleniyor"
        }

        layout.addView(title)
        layout.addView(statusText)
        layout.addView(startButton)

        setContentView(layout)
    }

    private fun showPhoneScreen() {

        val layout = createMainLayout()

        val title = createTitle(
            "TV Time Leap"
        )

        val subtitle = TextView(this).apply {
            text = "Telefon Kumandası"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
        }

        val ipInput = EditText(this).apply {
