package com.example.timer.util

object Utils {
    fun formatMillisecondsToMMSS(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

}

fun Long.minutesToMillis():Long{
    return this * 60 * 1000
}