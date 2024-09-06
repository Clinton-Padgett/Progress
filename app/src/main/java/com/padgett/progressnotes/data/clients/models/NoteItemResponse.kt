package com.padgett.progressnotes.data.clients.models

import com.google.firebase.firestore.PropertyName
import com.padgett.progressnotes.domain.clients.models.InvoiceStatus
import com.padgett.progressnotes.domain.clients.models.TimeType
import java.util.Date

data class NoteItemResponse(
    val id: String = "",
    @get:PropertyName("client_id") @set:PropertyName("client_id") var clientId: String = "",
    val type: TimeType = TimeType.TRAVEL,
    val start: Date = Date(),
    val minutes: Long = 0,
    val description: String = "",
    @get:PropertyName("invoice_status") @set:PropertyName("invoice_status") var invoiceStatus: InvoiceStatus = InvoiceStatus.DRAFT,
    @get:PropertyName("is_billable") @set:PropertyName("is_billable") var billable: Boolean = true,
    @get:PropertyName("is_deleted") @set:PropertyName("is_deleted") var deleted: Boolean = false
)