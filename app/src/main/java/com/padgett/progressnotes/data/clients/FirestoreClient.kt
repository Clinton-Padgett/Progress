package com.padgett.progressnotes.data.clients

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.padgett.progressnotes.data.clients.models.ClientResponse
import com.padgett.progressnotes.data.clients.models.NoteItemResponse
import com.padgett.progressnotes.data.clients.models.NotesResponse
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem
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
        const val USER_ID = "vpKv88ava7Fw8I0WVjQR"
        const val COMPANY_ID = "sIwd84Tq9qIwYBEH3dsI"

        const val COMPANIES_KEY = "companies"
        const val CLIENTS_KEY = "clients"
        const val NOTES_KEY = "notes"
        const val ITEMS_KEY = "items"
        const val NAME_KEY = "name"
        const val REFERENCE_KEY = "reference"
        const val ACTIVE_KEY = "active"
        const val ROLES_KEY = "roles"
        const val FIELD_CLIENT_ID = "client_id"
        const val FIELD_IS_DRAFT = "is_draft"
        const val FIELD_SUPERSEDED_BY = "superseded_by"
        const val FIELD_CREATED_BY = "created_by"
        const val FIELD_CREATED = "created"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_TYPE = "type"
        const val FIELD_START = "start"
        const val FIELD_MINUTES = "minutes"
        const val FIELD_INVOICE = "invoice"
        const val FIELD_BILLABLE = "billable"
        const val FIELD_DELETED = "deleted"
        const val FIELD_NOTES = "notes"

        const val CREATOR_VALUE = "creator"
    }

    private val clientsRef by lazy {
        firebaseFirestore.collection(COMPANIES_KEY)
            .document(COMPANY_ID)
            .collection(CLIENTS_KEY)
    }

    private val notesRef by lazy {
        firebaseFirestore.collection(COMPANIES_KEY)
            .document(COMPANY_ID)
            .collection(NOTES_KEY)
    }

    fun addClient(name: String, reference: String, isActive: Boolean) {
        clientsRef
            .add(
                hashMapOf(
                    NAME_KEY to name,
                    REFERENCE_KEY to reference,
                    ACTIVE_KEY to isActive,
                    ROLES_KEY to hashMapOf(
                        Firebase.auth.currentUser!!.uid to CREATOR_VALUE
                    )
                )
            )
    }

    fun updateClient(id: String, name: String, reference: String, isActive: Boolean) {
        clientsRef
            .document(id)
            .set(
                hashMapOf(
                    NAME_KEY to name,
                    REFERENCE_KEY to reference,
                    ACTIVE_KEY to isActive,
                    ROLES_KEY to hashMapOf(
                        Firebase.auth.currentUser!!.uid to CREATOR_VALUE
                    )
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
                    continuation.resumeWithException(GenericException())
                }
        }

    fun getDraftNotes(): Flow<List<Pair<String, NotesResponse>>> =
        notesRef
            .where(Filter.and(Filter.equalTo(FIELD_IS_DRAFT, true), Filter.equalTo(FIELD_SUPERSEDED_BY, "")))
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { it.id to it.toObject<NotesResponse>()!! }
            }

    suspend fun getNote(id: String): Result<NotesResponse> =
        suspendCoroutine { continuation ->
            notesRef
                .document(id)
                .get()
                .addOnSuccessListener {
                    val response = it.toObject<NotesResponse>()
                    if (response != null) {
                        continuation.resume(Result.success(response))
                    } else {
                        continuation.resumeWithException(GenericException())
                    }
                }.addOnFailureListener {
                    continuation.resumeWithException(GenericException())
                }
        }

    fun updateNote(noteId: String, notes: String) {
        notesRef
            .document(noteId)
            .update(
                mapOf(
                    FIELD_NOTES to notes
                )
            )
    }

    fun approveNote(noteId: String) {
        notesRef
            .document(noteId)
            .update(
                mapOf(
                    FIELD_IS_DRAFT to false
                )
            )
    }

    fun addNote(clientId: String): String {
        val ref = notesRef.document()
        ref.set(
            hashMapOf(
                FIELD_CLIENT_ID to clientId,
                FIELD_IS_DRAFT to true,
                FIELD_NOTES to "",
                FIELD_SUPERSEDED_BY to ""
            )
        )
        return ref.id
    }

    fun addNoteItem(clientId: String, noteId: String) {
        notesRef
            .document(noteId)
            .collection(ITEMS_KEY)
            .add(
                hashMapOf(
                    FIELD_CLIENT_ID to clientId,
                    FIELD_CREATED_BY to Firebase.auth.currentUser!!.uid,
                    FIELD_CREATED to Date(),
                    FIELD_DESCRIPTION to "",
                    FIELD_TYPE to TimeType.OFFICE,
                    FIELD_IS_DRAFT to true,
                    FIELD_START to Date(),
                    FIELD_MINUTES to 0,
                    FIELD_BILLABLE to true,
                    FIELD_INVOICE to true
                )
            )
    }

    fun getNoteItems(noteId: String): Flow<List<NoteItemResponse>> =
        notesRef
            .document(noteId)
            .collection(ITEMS_KEY)
            .snapshots()
            .map { snapshot ->
                snapshot.map { it.toObject<NoteItemResponse>().copy(id = it.id) }
            }

    fun updateNoteItem(noteId: String, clientNoteItem: ClientNoteItem) {
        notesRef
            .document(noteId)
            .collection(ITEMS_KEY)
            .document(clientNoteItem.id)
            .update(
                mapOf(
                    FIELD_TYPE to clientNoteItem.type,
                    FIELD_START to clientNoteItem.start,
                    FIELD_MINUTES to clientNoteItem.minutes,
                    FIELD_BILLABLE to clientNoteItem.billable,
                    FIELD_INVOICE to clientNoteItem.invoice,
                    FIELD_DESCRIPTION to clientNoteItem.description,
                    FIELD_DELETED to clientNoteItem.deleted
                )
            )
    }
}