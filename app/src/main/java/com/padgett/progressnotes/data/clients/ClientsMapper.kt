package com.padgett.progressnotes.data.clients

import com.padgett.progressnotes.data.clients.models.ClientResponse
import com.padgett.progressnotes.data.clients.models.NoteItemResponse
import com.padgett.progressnotes.data.clients.models.NoteResponse
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

fun NoteResponse.mapToDomain(): ClientNote =
    ClientNote(
        noteId = noteId,
        clientId = clientId,
        isDraft = isDraft,
        isApproved = isApproved,
        created = created,
        notes = notes
    )

fun NoteItemResponse.mapToDomain(): ClientNoteItem =
    ClientNoteItem(
        id = id,
        clientId = clientId,
        type = type,
        start = start,
        minutes = minutes,
        description = description,
        invoiceStatus = invoiceStatus,
        billable = billable,
        deleted = deleted
    )