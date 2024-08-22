package com.padgett.progressnotes.data.clients

import com.padgett.progressnotes.data.clients.models.ClientResponse
import com.padgett.progressnotes.data.clients.models.NoteItemResponse
import com.padgett.progressnotes.data.clients.models.NotesResponse
import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.domain.clients.models.ClientNote
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem
import kotlin.math.min

fun Pair<String, ClientResponse>.mapToDomain(): ClientDetails =
    ClientDetails(
        id = first,
        name = second.name,
        reference = second.reference,
        isActive = second.active
    )

fun Pair<String, NotesResponse>.mapToDomain(): ClientNote =
    ClientNote(
        id = first,
        clientId = second.clientId,
        isDraft = second.isDraft,
        created = second.created,
        notes = second.notes
    )

fun NoteItemResponse.mapToDomain(): ClientNoteItem =
    ClientNoteItem(
        id = id!!,
        clientId = clientId,
        type = type,
        start = start,
        minutes = minutes,
        description = description,
        invoice = invoice,
        billable = billable,
        deleted = deleted
    )