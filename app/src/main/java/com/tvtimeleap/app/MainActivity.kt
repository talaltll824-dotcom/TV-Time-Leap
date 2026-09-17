package com.tvtimeleap.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this)
        text.text = "TV Time Leap çalışıyor!"
        text.textSize = 24f
        text.gravity = android.view.Gravity.CENTER

        setContentView(text)
    }
}
