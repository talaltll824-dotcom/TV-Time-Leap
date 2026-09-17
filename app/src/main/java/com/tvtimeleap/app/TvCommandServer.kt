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
    private var running = false

    fun start() {
        if (running) return

        running = true

        thread(start = true) {
            try {
                serverSocket = ServerSocket(port)

                while (running) {
                    try {
                        val client = serverSocket?.accept() ?: break
                       
