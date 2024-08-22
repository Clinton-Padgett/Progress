package com.padgett.progressnotes.domain.clients.models

data class ClientDetails(
    val id: String,
    val name: String,
    val reference: String,
    val isActive: Boolean
)