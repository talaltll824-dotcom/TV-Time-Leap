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

        if (isTv()) {
            showTv()
        } else {
            showPhone()
        }
    }

    private fun isTv(): Boolean {
        val type =
            resources.configuration.uiMode and
                Configuration.UI_MODE_TYPE_MASK

        return type ==
            Configuration.UI_MODE_TYPE_TELEVISION
    }

    private fun showTv() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(40, 40, 40, 40)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "TV Time Leap"
        title.textSize = 30f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        val status = TextView(this)
        status.text = "Hazır"
        status.textSize = 18f
        status.gravity = Gravity.CENTER
        status.setPadding(0, 30, 0, 30)

        val start = Button(this)
        start.text = "TV TIME LEAP BAŞLAT"

        start.setOnClickListener {

            val serviceIntent =
                Intent(
                    this,
                    BufferService::class.java
                )

            ContextCompat.startForegroundService(
                this,
                serviceIntent
            )

            status.text =
                "Telefon bağlantısı bekleniyor"
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(start)

        setContentView(layout)
    }

    private fun showPhone() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(40, 40, 40, 40)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "TV Time Leap"
        title.textSize = 30f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        val ip = EditText(this)
        ip.hint = "TV IP adresi"
        ip.setSingleLine(true)

        val minutes = EditText(this)
        minutes.hint = "Kaç dakika geri?"
        minutes.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER
        minutes.setSingleLine(true)

        val status = TextView(this)
        status.text = "TV bağlantısı bekleniyor"
        status.gravity
