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

    override val pricePerHour: Float = 193.50F

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

    override suspend fun getNotesForClient(clientId: String): Flow<List<ClientNote>> =
        firestoreClient.getNotesByClientId(clientId).map { notes -> notes.map { it.mapToDomain() } }

    override suspend fun getNewNoteId(clientId: String): String = firestoreClient.addNote(clientId)

    override suspend fun getNote(id: String): Result<ClientNote> =
        firestoreClient.getNote(id).map { it.mapToDomain() }

    override suspend fun saveNote(noteId: String, isDraft: Boolean, isApproved: Boolean, notes: String) {
        withContext(ioDispatcher) {
            firestoreClient.updateNote(
                noteId = noteId,
                notes = notes,
                isApproved = isApproved,
                isDraft = isDraft
            )
        }
    }
    // endregion

    // region Note Items
    override suspend fun getNewNoteItemId(noteId: String): String = firestoreClient.addNoteItem(noteId)

    override suspend fun saveNoteItem(clientId: String, noteId: String, item: ClientNoteItem) {
        withContext(ioDispatcher) {
            with(item) {
                firestoreClient.updateNoteItem(
                    clientId = clientId,
                    noteId = noteId,
                    itemId = item.id,
                    description = description,
                    type = type,
                    start = start,
                    minutes = minutes,
                    billable = billable,
                    invoiceStatus = invoiceStatus,
                    isDeleted = false
                )
            }
        }
    }

    override suspend fun getNoteItems(noteId: String): Result<List<ClientNoteItem>> =
        firestoreClient.getNoteItems(noteId).map { items -> items.map { it.mapToDomain() } }

    // endregion

    // region Invoices
    override suspend fun getItemsReadyForInvoice(): Flow<List<ClientNoteItem>> =
        firestoreClient.getItemsReadyForInvoice()
            .map { clients ->
                clients.map { it.mapToDomain() }
            }

    override suspend fun getItemsReadyForInvoice(clientId: String): Flow<List<ClientNoteItem>> =
        firestoreClient.getItemsReadyForInvoice(clientId)
            .map { clients ->
                clients.map { it.mapToDomain() }
            }
    // endregion
}