package com.padgett.progressnotes.data.clients.models

data class ClientResponse(
    val name: String,
    val reference: String,
    val active: Boolean
) {
    constructor() : this("", "", true)
}