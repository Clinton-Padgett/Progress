package com.padgett.progressnotes.domain.clients.models

import java.util.Date

data class ClientNoteItem(
    val type: TimeType,
    val start: Date,
    val minutes: Long = 0,
    val description: String,
    val invoice: Boolean,
    val billable: Boolean,
    val deleted: Boolean
)