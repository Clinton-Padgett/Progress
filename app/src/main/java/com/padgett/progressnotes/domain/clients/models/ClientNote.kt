package com.padgett.progressnotes.domain.clients.models

import java.util.Date

data class ClientNote(
    val noteId: String,
    val clientId: String,
    val isDraft: Boolean,
    val isApproved: Boolean,
    val created: Date,
    val notes: String
)