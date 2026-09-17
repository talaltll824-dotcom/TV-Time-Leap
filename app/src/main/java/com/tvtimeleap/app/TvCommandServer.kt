package com.tvtimeleap.app

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class TvCommandServer(
    private val port: Int = 8765,
    private val onCommand: (String) -> Unit
) {

    private var serverSocket: ServerSocket? = null
    @Volatile
    private var running = false

    fun start() {
        if (running) return
        running = true

        thread {
            try {
                serverSocket = ServerSocket(port)

                while (running) {
                    val socket = try {
                        serverSocket?.accept()
                    } catch (_: Exception) {
                        null
                    }

                    if (socket != null) {
                        handleClient(socket)
                    }
                }
            } catch (_: Exception) {
                running = false
            }
        }
    }

    private fun handleClient(socket: Socket) {
        thread {
            try {
                socket.use { client ->
                    val reader = BufferedReader(
                        InputStreamReader(client.getInputStream())
                    )

                    val command = reader.readLine()
                        ?.trim()
                        ?.uppercase()

                    if (!command.isNullOrEmpty()) {
                        onCommand(command)
                    }

                    val writer = client.getOutputStream().bufferedWriter()
                    writer.write("OK")
                    writer.newLine()
                    writer.flush()
                }
            } catch (_: Exception) {
            }
        }
    }

    fun stop() {
        running = false

        try {
            serverSocket?.close()
        } catch (_: Exception) {
        }

        serverSocket = null
    }
}
