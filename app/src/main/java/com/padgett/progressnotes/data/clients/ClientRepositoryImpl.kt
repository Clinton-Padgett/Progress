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

    override suspend fun getNewNoteId(clientId: String): String = firestoreClient.addNote(clientId)

    override suspend fun getNote(id: String): Result<ClientNote> =
        firestoreClient.getNote(id).map { Pair(id, it).mapToDomain() }

    override suspend fun addNoteVersion(clientId: String, noteId: String, notes: String, isDraft: Boolean, isApproved: Boolean) =
        withContext(ioDispatcher) {
            firestoreClient.addNoteVersion(clientId = clientId, noteId = noteId, notes = notes, isDraft = isDraft, isApproved = isApproved)
        }
    // endregion

    // region Note Items
    override suspend fun getNoteItems(noteId: String, versionId: String): Result<List<ClientNoteItem>> =
        firestoreClient.getNoteItems(noteId, versionId).map { items -> items.map { it.mapToDomain() } }

    override suspend fun addNoteItems(clientId: String, noteId: String, versionId: String, items: List<ClientNoteItem>) {
        withContext(ioDispatcher) {
            items.forEach {
                firestoreClient.addNoteItem(
                    clientId = clientId,
                    noteId = noteId,
                    versionId = versionId,
                    description = it.description,
                    type = it.type,
                    start = it.start,
                    minutes = it.minutes,
                    billable = it.billable,
                    invoiceStatus = it.invoiceStatus
                )
            }
        }
    }
    // endregion

    // region Invoices
    override suspend fun getItemsReadyForInvoice(): Flow<List<ClientNoteItem>> =
        firestoreClient.getItemsReadyForInvoice()
            .map { clients ->
                clients.map { it.mapToDomain() }
            }

    override suspend fun getItemsReadyForInvoice(clientId: String): Flow<List<ClientNoteItem>> =
        firestoreClient.getItemsReadyForInvoice()
            .map { clients ->
                clients.map { it.mapToDomain() }
            }
    // endregion
}