package com.padgett.progressnotes.data.clients.models

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class NoteVersionResponse(
    @get:PropertyName("client_id") @set:PropertyName("client_id") var clientId: String = "",
    @get:PropertyName("version_id") @set:PropertyName("version_id") var versionId: String = "",
    @get:PropertyName("is_draft") @set:PropertyName("is_draft") var isDraft: Boolean = false,
    @get:PropertyName("is_approved") @set:PropertyName("is_approved") var isApproved: Boolean = false,
    var creator: String = "",
    val created: Date = Date(),
    val notes: String = ""
)