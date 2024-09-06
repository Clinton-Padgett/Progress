package com.padgett.progressnotes.data.clients.models

import com.google.firebase.firestore.PropertyName

data class ClientResponse(
    val name: String,
    val reference: String,
    @get:PropertyName("is_active") @set:PropertyName("is_active") var active: Boolean = true,
) {
    constructor() : this("", "", true)
}