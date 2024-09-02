package com.padgett.progressnotes.domain

import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.domain.clients.models.ClientNote
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    val pricePerHour: Float

    suspend fun addClient(name: String, reference: String, isActive: Boolean)
    suspend fun updateClient(id: String, name: String, reference: String, isActive: Boolean)

    fun getClients(): Flow<List<ClientDetails>>
    suspend fun getClient(id: String): Result<ClientDetails>

    fun getDraftNotes(): Flow<List<ClientNote>>
    suspend fun getNewNoteId(clientId: String): String
    suspend fun addNoteVersion(clientId: String, noteId: String, notes: String, isDraft: Boolean, isApproved: Boolean): String
    suspend fun getNote(id: String): Result<ClientNote>

    suspend fun addNoteItems(clientId: String, noteId: String, versionId: String, items: List<ClientNoteItem>)
    suspend fun getNoteItems(noteId: String, versionId: String): Result<List<ClientNoteItem>>

    suspend fun getItemsReadyForInvoice(): Flow<List<ClientNoteItem>>
    suspend fun getItemsReadyForInvoice(clientId: String): Flow<List<ClientNoteItem>>
}