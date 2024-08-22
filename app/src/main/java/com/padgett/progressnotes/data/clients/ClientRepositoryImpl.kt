package com.padgett.progressnotes.data.clients

import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.domain.clients.models.ClientNote
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val firestoreClient: FirestoreClient
) : ClientRepository {

    // region Clients
    override suspend fun addClient(name: String, reference: String, isActive: Boolean) {
        firestoreClient.addClient(name, reference, isActive)
    }

    override suspend fun updateClient(id: String, name: String, reference: String, isActive: Boolean) {
        firestoreClient.updateClient(id, name, reference, isActive)
    }

    override fun getClients(): Flow<List<ClientDetails>> =
        firestoreClient.getClients()
            .map { clients ->
                clients.map { it.mapToDomain() }
            }

    override suspend fun getClient(id: String): Result<ClientDetails> =
        firestoreClient.getClient(id).map { Pair(id, it).mapToDomain() }
    // endregion

    // region Notes
    override fun getDraftNotes(): Flow<List<ClientNote>> =
        firestoreClient.getDraftNotes().map { notes -> notes.map { it.mapToDomain() } }

    override suspend fun getNote(id: String): Result<ClientNote> =
        firestoreClient.getNote(id).map { Pair(id, it).mapToDomain() }

    override suspend fun updateNote(noteId: String, notes: String) {
        withContext(ioDispatcher) {
            firestoreClient.updateNote(noteId, notes)
        }
    }

    override suspend fun approveNote(noteId: String) {
        withContext(ioDispatcher) {
            firestoreClient.approveNote(noteId)
        }
    }

    override suspend fun addNote(clientId: String): String = firestoreClient.addNote(clientId)
    // endregion

    // region Note Items
    override fun getNoteItems(noteId: String): Flow<List<ClientNoteItem>> =
        firestoreClient.getNoteItems(noteId).map { items -> items.map { it.mapToDomain() } }

    override suspend fun addNoteItem(noteId: String) {
        withContext(ioDispatcher) {
            firestoreClient.getNote(noteId).onSuccess {
                firestoreClient.addNoteItem(it.clientId, noteId)
            }
        }
    }

    override suspend fun updateNoteItem(noteId: String, clientNoteItem: ClientNoteItem) {
        withContext(ioDispatcher) {
            firestoreClient.updateNoteItem(noteId, clientNoteItem)
        }
    }
    // endregion
}