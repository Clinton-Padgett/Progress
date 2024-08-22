package com.padgett.progressnotes.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.ProgressNavArgs
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem
import com.padgett.progressnotes.domain.clients.models.TimeType
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject

data class EditNoteUiState(
    val id: String? = null,
    val clientName: String = "",
    val notes: String = "",
    val isDraft: Boolean = true,
    val isValidEntry: Boolean = false,
    val items: List<Item> = listOf()
) {
    data class Item(
        val id: String,
        val type: TimeType = TimeType.OFFICE,
        val date: LocalDate = LocalDate.now(),
        val startTime: LocalTime,
        val minutes: Long = 0,
        val invoice: Boolean = true,
        val billable: Boolean = true,
        val description: String = "",
        val deleted: Date? = null
    )
}

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle = SavedStateHandle(),
    private val clientRepository: ClientRepository
) : BaseViewModel() {
    private val noteData = MutableStateFlow(EditNoteUiState())

    private val noteId: String = savedStateHandle[ProgressNavArgs.NOTE_ID_ARG]!!
    private lateinit var clientDetails: ClientDetails
    private var hasUserChanges = false

    val uiState: StateFlow<EditNoteUiState> =
        noteData
            .map {
                val isValid = it.items.all { it.deleted != null || (it.description.isNotBlank() && it.minutes > 0) } && it.notes.isNotBlank()
                it.copy(
                    isValidEntry = isValid,
                    items = it.items.filter { item -> item.deleted == null }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = EditNoteUiState()
            )

    init {
        viewModelScope.launch {
            clientRepository.getNote(noteId)
                .onSuccess { note ->
                    clientDetails = clientRepository.getClient(note.clientId).getOrThrow()
                    clientRepository.getNoteItems(noteId).collect { noteItems ->
                        val isValid = noteItems.all { it.deleted != null || (it.description.isNotBlank() && it.minutes > 0) } && note.notes.isNotBlank()
                        if (noteData.value.id == null) {
                            noteData.value = EditNoteUiState(
                                id = note.id,
                                clientName = clientDetails.name,
                                notes = note.notes,
                                isDraft = note.isDraft,
                                isValidEntry = isValid,
                                items = noteItems.map { note -> note.mapToUiItem() }
                            )
                        } else {
                            val existingNoteItemIds = noteData.value.items.map { it.id }
                            noteData.value = noteData.value.copy(
                                isValidEntry = isValid,
                                items = noteData.value.items.plus(noteItems.filter { it.id !in existingNoteItemIds }.map { it.mapToUiItem() })
                            )
                        }
                    }

                }
                .onFailure {
                    showErrorMessageToast(it)
                }
        }
    }

    fun onPause() {
        viewModelScope.launch {
            saveChanges()
        }
    }

    fun onAddItemClicked() {
        viewModelScope.launch {
            clientRepository.addNoteItem(noteId)
        }
    }

    fun onNotesChanged(text: String) {
        noteData.value = noteData.value.copy(notes = text)
        hasUserChanges = true
    }

    fun onTypeChanged(id: String, type: TimeType) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) note.copy(type = type) else note
                }
        )
        hasUserChanges = true
    }

    fun onStartDateChanged(id: String, date: LocalDate) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) note.copy(date = date) else note
                }
        )
        hasUserChanges = true
    }

    fun onStartTimeChanged(id: String, time: LocalTime) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) {
                        val newMinutes = if (note.minutes > 0L) note.minutes + time.until(note.startTime, ChronoUnit.MINUTES) else 0L
                        if (newMinutes > 0L) {
                            note.copy(minutes = newMinutes, startTime = time)
                        } else {
                            note.copy(minutes = 0, startTime = time)
                        }
                    } else note
                }
        )
        hasUserChanges = true
    }

    fun onMinutesChanged(id: String, minutes: Long) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) note.copy(minutes = minutes) else note
                }
        )
        hasUserChanges = true
    }

    fun onDescriptionChanged(id: String, text: String) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) note.copy(description = text) else note
                }
        )
        hasUserChanges = true
    }

    fun onInvoiceClicked(id: String) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) note.copy(invoice = !note.invoice) else note
                }
        )
        hasUserChanges = true
    }

    fun onBillableClicked(id: String) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) note.copy(billable = !note.billable) else note
                }
        )
        hasUserChanges = true
    }

    fun onDeleteItemClicked(id: String) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .map { note ->
                    if (note.id == id) note.copy(deleted = Date()) else note
                }
        )
        hasUserChanges = true
    }

    fun onCloseClicked(navigate: () -> Unit) {
        if (hasUserChanges) {
            showLoadingOverlay()
            viewModelScope.launch {
                saveChanges()
                navigate.invoke()
                hideLoadingOverlay()
            }
        } else {
            navigate.invoke()
        }
    }

    fun onApproveClicked(navigate: () -> Unit) {
        if (hasUserChanges) {
            showLoadingOverlay()
            viewModelScope.launch {
                saveChanges()
                clientRepository.approveNote(noteId)
                navigate.invoke()
                hideLoadingOverlay()
            }
        } else {
            navigate.invoke()
        }
    }

    private suspend fun saveChanges() {
        if (hasUserChanges) {
            noteData.value.items.forEach {
                clientRepository.updateNoteItem(noteId, it.mapToDomain())
            }
            clientRepository.updateNote(noteId, noteData.value.notes)
        }
    }

    private fun EditNoteUiState.Item.mapToDomain(): ClientNoteItem =
        ClientNoteItem(
            id = id,
            clientId = clientDetails.id,
            type = type,
            start = Date.from(LocalDateTime.of(date, startTime).atZone(ZoneId.systemDefault()).toInstant()),
            minutes = minutes,
            description = description,
            invoice = invoice,
            billable = billable,
            deleted = deleted
        )

    private fun ClientNoteItem.mapToUiItem(): EditNoteUiState.Item {
        val startDateTime = Instant.ofEpochMilli(start.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return EditNoteUiState.Item(
            id = id,
            type = type,
            date = startDateTime.toLocalDate(),
            startTime = startDateTime.toLocalTime(),
            minutes = minutes,
            description = description,
            invoice = invoice,
            billable = billable,
            deleted = deleted
        )
    }
}