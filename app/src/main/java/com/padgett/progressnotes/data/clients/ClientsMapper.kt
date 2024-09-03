package com.padgett.progressnotes.data.clients

import com.padgett.progressnotes.data.clients.models.ClientResponse
import com.padgett.progressnotes.data.clients.models.NoteItemResponse
import com.padgett.progressnotes.data.clients.models.NoteVersionResponse
import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.domain.clients.models.ClientNote
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem

fun Pair<String, ClientResponse>.mapToDomain(): ClientDetails =
    ClientDetails(
        id = first,
        name = second.name,
        reference = second.reference,
        isActive = second.active
    )

fun Pair<String, NoteVersionResponse>.mapToDomain(): ClientNote =
    ClientNote(
        noteId = first,
        versionId = second.versionId,
        clientId = second.clientId,
        isDraft = second.isDraft,
        isApproved = second.isApproved,
        created = second.created,
        notes = second.notes
    )

fun NoteItemResponse.mapToDomain(): ClientNoteItem =
    ClientNoteItem(
        id = id,
        clientId = clientId,
        type = type,
        start = start,
        minutes = minutes,
        description = description,
        invoiceStatus = invoice,
        billable = billable,
        deleted = deleted
    )