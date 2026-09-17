package com.tvtimeleap.app

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.concurrent.thread

class TvCommandClient {

    fun sendCommand(
        tvIp: String,
        command: String,
        onResult: (Boolean, String) -> Unit
    ) {
        thread {
            try {
                Socket().use { socket ->

                    socket.connect(
                        InetSocketAddress(tvIp, PORT),
                        5000
                    )

                    val writer =
                        socket.getOutputStream()
                            .bufferedWriter()

                    writer.write(command)
                    writer.newLine()
                    writer.flush()

                    val reader = BufferedReader(
                        InputStreamReader(
                            socket.getInputStream()
                        )
                    )

                    val response =
                        reader.readLine() ?: ""

                    if (response == "OK") {
                        onResult(
                            true,
                            "Komut TV'ye gönderildi"
                        )
                    } else {
                        onResult(
                            false,
                            "TV cevap vermedi"
                        )
                    }
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    "TV bağlantısı kurulamadı"
                )
            }
        }
    }

    fun seekBack(
        tvIp: String,
        minutes: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        if (minutes <= 0) {
            onResult(
                false,
                "Dakika 0'dan büyük olmalı"
            )
            return
        }

        sendCommand(
            tvIp,
            "BACK:$minutes",
            onResult
        )
    }

    fun goLive(
        tvIp: String,
        onResult: (Boolean, String) -> Unit
    ) {
        sendCommand(
            tvIp
