package com.padgett.progressnotes.domain.clients.models

import java.util.Date

data class ClientNoteItem(
    val id: String,
    val clientId: String,
    val type: TimeType,
    val start: Date,
    val minutes: Long = 0,
    val description: String,
    val invoiceStatus: InvoiceStatus,
    val billable: Boolean,
    val deleted: Boolean
)