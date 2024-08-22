package com.padgett.progressnotes.domain

import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.domain.clients.models.ClientNote
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    suspend fun addClient(name: String, reference: String, isActive: Boolean)
    suspend fun updateClient(id: String, name: String, reference: String, isActive: Boolean)

    fun getClients(): Flow<List<ClientDetails>>
    suspend fun getClient(id: String): Result<ClientDetails>

    fun getDraftNotes(): Flow<List<ClientNote>>
    suspend fun getNote(id: String): Result<ClientNote>
    suspend fun updateNote(noteId: String, notes: String)
    suspend fun approveNote(noteId: String)
    suspend fun addNote(clientId: String): String

    fun getNoteItems(noteId: String): Flow<List<ClientNoteItem>>
    suspend fun addNoteItem(noteId: String)
    suspend fun updateNoteItem(noteId: String, clientNoteItem: ClientNoteItem)
}