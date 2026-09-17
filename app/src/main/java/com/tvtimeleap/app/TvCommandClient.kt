package com.tvtimeleap.app

import java.net.InetSocketAddress
import java.net.Socket
import kotlin.concurrent.thread

class TvCommandClient {

    fun send(
        tvIp: String,
        command: String,
        onResult: (Boolean) -> Unit
    ) {
        thread {
            var success = false

            try {
                Socket().use { socket ->

                    socket.connect(
                        InetSocketAddress(tvIp, 8765),
                        5000
                    )

                    val writer =
                        socket.getOutputStream()
                            .bufferedWriter()

                    writer.write(command)
                    writer.newLine()
                    writer.flush()

                    val reader =
                        socket.getInputStream()
                            .bufferedReader()

                    val response =
                        reader.readLine()

                    success = response == "OK"
                }
            } catch (_: Exception) {
                success = false
            }

            onResult(success)
        }
    }

    fun seekBack(
        tvIp: String,
        minutes: Int,
        onResult: (Boolean) -> Unit
    ) {
        send(
            tvIp,
            "BACK:$minutes",
            onResult
        )
    }

    fun goLive(
        tvIp: String,
        onResult: (Boolean) -> Unit
    ) {
        send(
            tvIp,
            "LIVE",
            onResult
        )
    }

    fun playPause(
        tvIp: String,
        onResult: (Boolean) -> Unit
    ) {
        send(
            tvIp,
            "PLAY_PAUSE",
            onResult
        )
    }
}
