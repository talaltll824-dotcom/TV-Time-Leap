package com.tvtimeleap.app

import android.content.Context
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.media.tv.TvView
import android.net.Uri
import android.provider.BaseColumns

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

                    override fun onConnectionFailed(
                        inputId: String
                    ) {
                        onResult(
                            "TUNER BAGLANTISI BASARISIZ"
                        )
                    }

                    override fun onVideoAvailable(
                        inputId: String
                    ) {
                        onResult(
                            "KANAL ACILDI - TIMESHIFT BEKLENIYOR"
                        )
                    }

                    override fun onVideoUnavailable(
                        inputId: String,
                        reason: Int
                    ) {
                        onResult(
                            "KANAL VIDEO BEKLENIYOR - KOD: $reason"
                        )
                    }

                    override fun onTimeShiftStatusChanged(
                        inputId: String,
                        status: Int
                    ) {

                        val result =
                            when (status) {

                                TvInputManager.TIME_SHIFT_STATUS_AVAILABLE ->
                                    "TIMESHIFT KULLANILABILIR"

                                TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE ->
                                    "TIMESHIFT SU AN KULLANILAMIYOR"

                                TvInputManager.TIME_SHIFT_STATUS_UNSUPPORTED ->
                                    "TIMESHIFT DESTEKLENMIYOR"

                                else ->
                                    "TIMESHIFT DURUM KODU: $status"
                            }

                        onResult(result)
                    }
                }
            )

            val channel =
                findChannel(
                    tuner.id
                )

            if (channel == null) {

                onResult(
                    "TUNER VAR AMA KANAL LISTESINE ERISILEMEDI"
                )

                return
            }

            onResult(
                "KANAL BULUNDU - BAGLANILIYOR..."
            )

            tvView.tune(
                tuner.id,
                channel
            )

        } catch (e: SecurityException) {

            onResult(
                "KANAL ERISIM IZNI ENGELLENDI"
            )

        } catch (e: Exception) {

            onResult(
                "TEST HATASI: " +
                    (e.message ?: "Bilinmeyen hata")
            )
        }
    }

    private fun findChannel(
        inputId: String
    ): Uri? {

        val projection =
            arrayOf(
                BaseColumns._ID,
                TvContract.Channels.COLUMN_INPUT_ID
            )

        val cursor =
            context.contentResolver.query(
                TvContract.Channels.CONTENT_URI,
                projection,
                "${TvContract.Channels.COLUMN_INPUT_ID} = ?",
                arrayOf(inputId),
                null
            )

        cursor?.use {

            if (it.moveToFirst()) {

                val idIndex =
                    it.getColumnIndex(
                        BaseColumns._ID
                    )

                if (idIndex >= 0) {

                    val channelId =
                        it.getLong(
                            idIndex
                        )

                    return TvContract.buildChannelUri(
                        channelId
                    )
                }
            }
        }

        return null
    }
}
