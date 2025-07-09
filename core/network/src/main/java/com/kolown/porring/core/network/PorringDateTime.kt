package com.kolown.porring.core.network

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object PorringDateTime {

    fun getNowDateTimeString(): String = formatCurrentTime(TimeZone.getDefault())

    fun getNowDateTimeUTCString(): String = formatCurrentTime(TimeZone.getTimeZone(UTC))

    private fun formatCurrentTime(
        timeZone: TimeZone,
    ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        formatTimeApi26(timeZone)
    } else {
        formatTimeApi19(timeZone)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTimeApi26(
        timeZone: TimeZone
    ): String = if (timeZone == TimeZone.getTimeZone(UTC)) {
        ZonedDateTime.now(ZoneOffset.UTC).toString()
    } else {
        val formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN)
        ZonedDateTime.now().format(formatter)
    }

    private fun formatTimeApi19(
        timeZone: TimeZone,
    ): String {
        val calendar = Calendar.getInstance(timeZone)

        val pattern: String = when (timeZone.id) {
            UTC -> UTC_PATTERN19
            else -> DEFAULT_PATTERN19
        }

        return String.format(
            Locale.US,
            pattern,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
    }

    private const val UTC = "UTC"
    private const val DEFAULT_PATTERN = "yyyyMMddHHmmss"
    private const val DEFAULT_PATTERN19 = "%04d%02d%02d%02d%02d%02d"
    private const val UTC_PATTERN19 = "%04d-%02d-%02dT%02d:%02d:%02d"
}
