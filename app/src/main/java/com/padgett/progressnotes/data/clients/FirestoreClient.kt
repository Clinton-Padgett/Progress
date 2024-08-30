package com.padgett.progressnotes.data.clients

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.padgett.progressnotes.data.clients.models.ClientResponse
import com.padgett.progressnotes.data.clients.models.NoteItemResponse
import com.padgett.progressnotes.data.clients.models.NoteVersionResponse
import com.padgett.progressnotes.data.mapToDomainException
import com.padgett.progressnotes.domain.clients.models.InvoiceStatus
import com.padgett.progressnotes.domain.clients.models.TimeType
import com.padgett.progressnotes.domain.common.exceptions.GenericException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreClient @Inject constructor(private val firebaseFirestore: FirebaseFirestore) {

    private companion object {
        const val COLLECTION_CLIENTS = "clients"
        const val COLLECTION_NOTES = "notes"
        const val COLLECTION_VERSIONS = "versions"
        const val COLLECTION_ITEMS = "items"

        const val FIELD_NAME = "name"
        const val FIELD_REFERENCE = "reference"
        const val FIELD_IS_ACTIVE = "is_active"
        const val FIELD_COMPANY_ID = "company_id"
        const val FIELD_CLIENT_ID = "client_id"
        const val FIELD_NOTE_ID = "note_id"
        const val FIELD_VERSION_ID = "version_id"
        const val FIELD_IS_DRAFT = "is_draft"
        const val FIELD_IS_APPROVED = "is_approved"
        const val FIELD_CREATOR = "creator"
        const val FIELD_CREATED = "created"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_TYPE = "type"
        const val FIELD_START = "start"
        const val FIELD_MINUTES = "minutes"
        const val FIELD_INVOICE = "invoice"
        const val FIELD_BILLABLE = "billable"
        const val FIELD_NOTES = "notes"

        const val VALUE_COMPANY_ID = "sIwd84Tq9qIwYBEH3dsI"
    }

    private val clientsRef by lazy { firebaseFirestore.collection(COLLECTION_CLIENTS) }

    private val notesRef by lazy { firebaseFirestore.collection(COLLECTION_NOTES) }

    private fun getUserId(): String = Firebase.auth.currentUser!!.uid

    fun addClient(name: String, reference: String, isActive: Boolean) {
        clientsRef
            .add(
                hashMapOf(
                    FIELD_COMPANY_ID to VALUE_COMPANY_ID,
                    FIELD_NAME to name,
                    FIELD_REFERENCE to reference,
                    FIELD_IS_ACTIVE to isActive,
                    FIELD_CREATOR to getUserId()
                )
            )
    }

    fun updateClient(id: String, name: String, reference: String, isActive: Boolean) {
        clientsRef
            .document(id)
            .set(
                hashMapOf(
                    FIELD_COMPANY_ID to VALUE_COMPANY_ID,
                    FIELD_NAME to name,
                    FIELD_REFERENCE to reference,
                    FIELD_IS_ACTIVE to isActive,
                    FIELD_CREATOR to getUserId()
                )
            )
    }

    fun getClients(): Flow<List<Pair<String, ClientResponse>>> =
        clientsRef
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { it.id to it.toObject<ClientResponse>()!! }
            }

    suspend fun getClient(id: String): Result<ClientResponse> =
        suspendCoroutine { continuation ->
            clientsRef
                .document(id)
                .get()
                .addOnSuccessListener {
                    val response = it.toObject<ClientResponse>()
                    if (response != null) {
                        continuation.resume(Result.success(response))
                    } else {
                        continuation.resumeWithException(GenericException())
                    }
                }.addOnFailureListener {
                    continuation.resumeWithException(it.mapToDomainException())
                }
        }

    fun getDraftNotes(): Flow<List<Pair<String, NoteVersionResponse>>> =
        firebaseFirestore.collectionGroup(COLLECTION_VERSIONS)
            .where(
                Filter.and(
                    Filter.equalTo(FIELD_COMPANY_ID, VALUE_COMPANY_ID),
                    Filter.equalTo(FIELD_CREATOR, getUserId())
                )
            )
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map {
                    val noteId = it.reference.path.split("/")[1]
                    noteId to it.toObject<NoteVersionResponse>()!!
                }.groupBy { it.first }
                    .mapNotNull { it.value.maxByOrNull { it.second.created }?.takeIf { it.second.isDraft } }
            }

    suspend fun getNote(noteId: String): Result<NoteVersionResponse> =
        suspendCoroutine { continuation ->
            firebaseFirestore.collectionGroup(COLLECTION_VERSIONS)
                .where(Filter.equalTo(FIELD_NOTE_ID, noteId))
                .get()
                .addOnSuccessListener { snapshot ->
                    val response = snapshot
                        .map { it.toObject<NoteVersionResponse>() }
                        .maxByOrNull { it.created }!!
                    continuation.resume(Result.success(response))
                }.addOnFailureListener {
                    continuation.resumeWithException(it.mapToDomainException())
                }
        }

    fun addNote(clientId: String): String {
        val ref = notesRef.document()
        ref.set(
            hashMapOf(
                FIELD_COMPANY_ID to VALUE_COMPANY_ID,
                FIELD_CLIENT_ID to clientId,
                FIELD_NOTE_ID to ref.id,
                FIELD_CREATED to Date(),
                FIELD_CREATOR to getUserId()
            )
        )
        return ref.id
    }

    fun addNoteVersion(
        clientId: String,
        noteId: String,
        notes: String,
        isDraft: Boolean,
        isApproved: Boolean
    ): String {
        val ref = notesRef
            .document(noteId)
            .collection(COLLECTION_VERSIONS)
            .document()
        ref.set(
            hashMapOf(
                FIELD_COMPANY_ID to VALUE_COMPANY_ID,
                FIELD_CLIENT_ID to clientId,
                FIELD_NOTE_ID to noteId,
                FIELD_VERSION_ID to ref.id,
                FIELD_CREATED to Date(),
                FIELD_CREATOR to getUserId(),
                FIELD_NOTES to notes,
                FIELD_IS_DRAFT to isDraft,
                FIELD_IS_APPROVED to isApproved
            )
        )
        return ref.id
    }

    fun addNoteItem(
        clientId: String,
        noteId: String,
        versionId: String,
        description: String,
        type: TimeType,
        start: Date,
        minutes: Long,
        billable: Boolean,
        invoiceStatus: InvoiceStatus
    ) {
        notesRef
            .document(noteId)
            .collection(COLLECTION_VERSIONS)
            .document(versionId)
            .collection(COLLECTION_ITEMS)
            .add(
                hashMapOf(
                    FIELD_COMPANY_ID to VALUE_COMPANY_ID,
                    FIELD_CLIENT_ID to clientId,
                    FIELD_DESCRIPTION to description,
                    FIELD_TYPE to type,
                    FIELD_START to start,
                    FIELD_MINUTES to minutes,
                    FIELD_BILLABLE to billable,
                    FIELD_INVOICE to invoiceStatus
                )
            )
    }

    suspend fun getNoteItems(noteId: String, versionId: String): Result<List<NoteItemResponse>> =
        suspendCoroutine { continuation ->
            notesRef
                .document(noteId)
                .collection(COLLECTION_VERSIONS)
                .document(versionId)
                .collection(COLLECTION_ITEMS)
                .get()
                .addOnSuccessListener {
                    continuation.resume(Result.success(it.map { item -> item.toObject<NoteItemResponse>() }))
                }.addOnFailureListener {
                    continuation.resumeWithException(it.mapToDomainException())
                }
        }

    fun getItemsReadyForInvoice(): Flow<List<NoteItemResponse>> =
        firebaseFirestore.collectionGroup(COLLECTION_ITEMS)
            .where(
                Filter.and(
                    Filter.equalTo(FIELD_COMPANY_ID, VALUE_COMPANY_ID),
                    Filter.equalTo(FIELD_INVOICE, InvoiceStatus.READY)
                )
            )
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { it.toObject<NoteItemResponse>()!! }
            }
}