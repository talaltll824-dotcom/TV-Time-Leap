package com.tvtimeleap.app

import android.content.Context
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.media.tv.TvView

class TimeShiftTester(
    private val context: Context,
    private val tvView: TvView,
    private val onResult: (String) -> Unit
) {

    fun start() {
        try {
            val manager =
                context.getSystemService(
                    Context.TV_INPUT_SERVICE
                ) as TvInputManager

            val tuner =
                manager.tvInputList.firstOrNull {
                    it.type == TvInputInfo.TYPE_TUNER
                }

            if (tuner == null) {
                onResult("TUNER BULUNAMADI")
                return
            }

            tvView.setCallback(
                object : TvView.TvInputCallback() {

                    override fun onTimeShiftStatusChanged(
                        inputId: String,
                        status: Int
                    ) {
                        val text =
                            when (status) {

                                TvInputManager.TIME_SHIFT_STATUS_AVAILABLE ->
                                    "TIMESHIFT KULLANILABILIR"

                                TvInputManager.TIME_SHIFT_STATUS_UNSUPPORTED ->
                                    "TIMESHIFT DESTEKLENMIYOR"

                                TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE ->
                                    "TIMESHIFT SU AN KULLANILAMIYOR"

                                else ->
                                    "TIMESHIFT DURUMU: $status"
                            }

                        onResult(text)
                    }

                    override fun onConnectionFailed(
                        inputId: String
                    ) {
                        onResult("TUNER BAGLANTISI BASARISIZ")
                    }
                }
            )

            onResult(
                "TUNER HAZIR: ${tuner.id}"
            )

        } catch (e: Exception) {
            onResult(
                "TEST HATASI: ${e.message ?: "Bilinmeyen hata"}"
            )
        }
    }
}
