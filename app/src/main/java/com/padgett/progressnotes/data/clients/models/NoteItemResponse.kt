package com.padgett.progressnotes.data.clients.models

import com.google.firebase.firestore.PropertyName
import com.padgett.progressnotes.domain.clients.models.TimeType
import java.util.Date

data class NoteItemResponse(
    val id: String? = null,
    @get:PropertyName("client_id") @set:PropertyName("client_id") var clientId: String = "",
    @get:PropertyName("created_by") @set:PropertyName("created_by") var createdBy: String = "",
    val type: TimeType = TimeType.TRAVEL,
    val created: Date = Date(),
    val start: Date = Date(),
    val minutes: Long = 0,
    val description: String = "",
    val invoice: Boolean = true,
    val billable: Boolean = true,
    val deleted: Date? = null
)