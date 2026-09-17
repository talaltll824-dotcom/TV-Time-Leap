package com.tvtimeleap.app

import android.content.Context
import java.io.File

class TimeShiftBuffer(
    context: Context
) {

    private val bufferDirectory =
        File(
            context.cacheDir,
            "tv_time_leap_buffer"
        )

    init {
        if (!bufferDirectory.exists()) {
            bufferDirectory.mkdirs()
        }
    }

    fun getDirectory(): File {
        return bufferDirectory
    }

    fun createSegmentFile(): File {

        val fileName =
            "segment_${System.currentTimeMillis()}.ts"

        return File(
            bufferDirectory,
            fileName
        )
    }

    fun cleanOldSegments(
        maximumMinutes: Int
    ) {

        if (maximumMinutes <= 0) {
            return
        }

        val maximumAge =
            maximumMinutes.toLong() *
                60_000L

        val limit =
            System.currentTimeMillis() -
                maximumAge

        val files =
            bufferDirectory.listFiles()
                ?: return

        for (file in files) {

            if (
                file.isFile &&
                file.lastModified() < limit
            ) {
                file.delete()
            }
        }
    }

    fun clear() {

        val files =
            bufferDirectory.listFiles()
                ?: return

        for (file in files) {
            if (file.isFile) {
                file.delete()
            }
        }
    }

    fun getBufferSizeBytes(): Long {

        val files =
            bufferDirectory.listFiles()
                ?: return 0L

        var total = 0L

        for (file in files) {
            if (file.isFile) {
                total += file.length()
            }
        }

        return total
    }

    fun getOldestSegment(): File? {

        val files =
            bufferDirectory.listFiles()
                ?.filter {
                    it.isFile
                }
                ?: return null

        return files.minByOrNull {
            it.lastModified()
        }
    }

    fun getNewestSegment(): File? {

        val files =
            bufferDirectory.listFiles()
                ?.filter {
                    it.isFile
                }
                ?: return null

        return files.maxByOrNull {
            it.lastModified()
        }
    }
}
