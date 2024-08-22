package com.padgett.progressnotes

import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Calendar

private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000

fun Calendar.toEndOfDay(): Calendar =
    apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

fun ZonedDateTime.toEpochMillis(): Long = toEpochSecond() * 1000

fun LocalDate.toEpochMillis(): Long = toEpochDay() * MILLIS_PER_DAY + MILLIS_PER_DAY / 2