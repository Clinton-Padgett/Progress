package com.padgett.progressnotes.data.clients.models

import com.padgett.progressnotes.domain.clients.models.TimeType
import java.util.Date

data class NoteItemResponse(
    val type: TimeType = TimeType.TRAVEL,
    val start: Date = Date(),
    val minutes: Long = 0,
    val description: String = "",
    val invoice: Boolean = true,
    val billable: Boolean = true,
    val deleted: Boolean = false
)