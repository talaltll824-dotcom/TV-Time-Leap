package com.tvtimeleap.app

import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.media.tv.TvView
import android.net.Uri

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

            val tunerId = tuner.id

            onResult(
                "TUNER BULUNDU\nKANAL ARANIYOR..."
            )

            tryChannelAccess(tunerId)

        } catch (e: Exception) {

            onResult(
                "TUNER HATASI:\n" +
                    (e.message ?: e.javaClass.simpleName)
            )
        }
    }

    private fun tryChannelAccess(
        tunerId: String
    ) {

        var cursor: Cursor? = null

        try {

            val projection = arrayOf(
                TvContract.Channels._ID,
                TvContract.Channels.COLUMN_DISPLAY_NUMBER,
                TvContract.Channels.COLUMN_DISPLAY_NAME,
                TvContract.Channels.COLUMN_INPUT_ID
            )

            cursor =
                context.contentResolver.query(
                    TvContract.Channels.CONTENT_URI,
                    projection,
                    "${TvContract.Channels.COLUMN_INPUT_ID} = ?",
                    arrayOf(tunerId),
                    null
                )

            if (cursor == null) {

                onResult(
                    "KANAL VERİTABANINA ERİŞİLEMEDİ"
                )

                return
            }

            if (!cursor.moveToFirst()) {

                onResult(
                    "TUNER VAR AMA KANAL LİSTESİ " +
                        "UYGULAMAYA AÇIK DEĞİL"
                )

                return
            }

            val idIndex =
                cursor.getColumnIndex(
                    TvContract.Channels._ID
                )

            val numberIndex =
                cursor.getColumnIndex(
                    TvContract.Channels.COLUMN_DISPLAY_NUMBER
                )

            val nameIndex =
                cursor.getColumnIndex(
                    TvContract.Channels.COLUMN_DISPLAY_NAME
                )

            if (idIndex < 0) {

                onResult(
                    "KANAL ID OKUNAMADI"
                )

                return
            }

            val channelId =
                cursor.getLong(idIndex)

            val channelNumber =
                if (numberIndex >= 0) {
                    cursor.getString(numberIndex)
                        ?: "?"
                } else {
                    "?"
                }

            val channelName =
                if (nameIndex >= 0) {
                    cursor.getString(nameIndex)
                        ?: "Bilinmeyen kanal"
                } else {
                    "Bilinmeyen kanal"
                }

            val channelUri: Uri =
                TvContract.buildChannelUri(
                    channelId
                )

            onResult(
                "KANAL BULUNDU\n" +
                    "$channelNumber - $channelName\n" +
                    "AÇILIYOR..."
            )

            tvView.tune(
                tunerId,
                channelUri
            )

        } catch (e: SecurityException) {

            onResult(
                "KANAL ERİŞİMİ ANDROID TARAFINDAN ENGELLENDİ\n" +
                    e.javaClass.simpleName
            )

        } catch (e: Exception) {

            onResult(
                "KANAL TEST HATASI:\n" +
                    (e.message ?: e.javaClass.simpleName)
            )

        } finally {

            try {
                cursor?.close()
            } catch (_: Exception) {
            }
        }
    }
}
