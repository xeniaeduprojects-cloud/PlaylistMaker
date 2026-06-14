package com.praktikum.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale.getDefault

private const val MILLIS_IN_SECOND = 1000L

private fun minutesSecondsFormatter(): SimpleDateFormat = SimpleDateFormat("mm:ss", getDefault())

fun formatMillisToMinutesSeconds(millis: Long): String = minutesSecondsFormatter().format(millis.coerceAtLeast(0L))

fun formatSecondsToMinutesSeconds(seconds: Int): String {
    val safeMillis = seconds.coerceAtLeast(0).toLong() * MILLIS_IN_SECOND
    return formatMillisToMinutesSeconds(safeMillis)
}
