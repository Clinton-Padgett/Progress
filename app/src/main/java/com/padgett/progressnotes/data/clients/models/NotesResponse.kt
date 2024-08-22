package com.padgett.progressnotes.data.clients.models

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class NotesResponse(
    @get:PropertyName("client_id") @set:PropertyName("client_id") var clientId: String = "",
    @get:PropertyName("is_draft") @set:PropertyName("is_draft") var isDraft: Boolean = false,
    @get:PropertyName("created_by") @set:PropertyName("created_by") var createdBy: String = "",
    val created: Date = Date(),
    val notes: String = ""
)