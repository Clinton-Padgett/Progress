package com.padgett.progressnotes.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.ProgressNavArgs
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.domain.clients.models.ClientNoteItem
import com.padgett.progressnotes.domain.clients.models.InvoiceStatus
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
    val clientName: String = "",
    val notes: String = "",
    val isDraft: Boolean = true,
    val isApproved: Boolean = false,
    val isValidEntry: Boolean = false,
    val items: List<Item> = listOf()
) {
    data class Item(
        val type: TimeType = TimeType.OFFICE,
        val date: LocalDate = LocalDate.now(),
        val startTime: LocalTime = LocalTime.now(),
        val minutes: Long = 0,
        val invoiceStatus: InvoiceStatus = InvoiceStatus.DRAFT,
        val billable: Boolean = true,
        val description: String = "",
        val deleted: Boolean = false
    )
}

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle = SavedStateHandle(),
    private val clientRepository: ClientRepository
) : BaseViewModel() {
    private val noteData = MutableStateFlow(EditNoteUiState())

    private val clientId: String = savedStateHandle[ProgressNavArgs.CLIENT_ID_ARG]!!
    private var noteId: String? = savedStateHandle[ProgressNavArgs.NOTE_ID_ARG]
    private var hasUserChanges = false

    val uiState: StateFlow<EditNoteUiState> =
        noteData
            .map {
                val isValid = it.items.all { it.deleted || (it.description.isNotBlank() && it.minutes > 0) } && it.notes.isNotBlank()
                it.copy(
                    isValidEntry = isValid,
                    items = it.items
                        .filterNot { item -> item.deleted }
                        .sortedBy { LocalDateTime.of(it.date, it.startTime) }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = EditNoteUiState()
            )

    init {
        showLoadingOverlay()
        viewModelScope.launch {
            clientRepository.getClient(clientId)
                .onSuccess {
                    noteData.value = EditNoteUiState(clientName = it.name)
                    getNote()
                }.onFailure {
                    hideLoadingOverlay()
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
        noteData.value = noteData.value.copy(
            items = noteData.value.items.plus(EditNoteUiState.Item())
        )
        hasUserChanges = true
    }

    fun onNotesChanged(text: String) {
        noteData.value = noteData.value.copy(notes = text)
        hasUserChanges = true
    }

    fun onTypeChanged(index: Int, type: TimeType) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) note.copy(type = type) else note
                }
        )
        hasUserChanges = true
    }

    fun onStartDateChanged(index: Int, date: LocalDate) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) note.copy(date = date) else note
                }
        )
        hasUserChanges = true
    }

    fun onStartTimeChanged(index: Int, time: LocalTime) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) {
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

    fun onMinutesChanged(index: Int, minutes: Long) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) note.copy(minutes = minutes) else note
                }
        )
        hasUserChanges = true
    }

    fun onDescriptionChanged(index: Int, text: String) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) note.copy(description = text) else note
                }
        )
        hasUserChanges = true
    }

    fun onInvoiceClicked(index: Int) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) {
                        note.copy(invoiceStatus = if (note.invoiceStatus == InvoiceStatus.DRAFT) InvoiceStatus.DO_NOT_INVOICE else InvoiceStatus.DRAFT)
                    } else {
                        note
                    }
                }
        )
        hasUserChanges = true
    }

    fun onBillableClicked(index: Int) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) note.copy(billable = !note.billable) else note
                }
        )
        hasUserChanges = true
    }

    fun onDeleteItemClicked(index: Int) {
        noteData.value = noteData.value.copy(
            items = noteData.value.items
                .mapIndexed { i, note ->
                    if (index == i) note.copy(deleted = true) else note
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
        if (hasUserChanges || !noteData.value.isApproved) {
            showLoadingOverlay()
            viewModelScope.launch {
                if (!noteData.value.isApproved) {
                    hasUserChanges = true
                    noteData.value = noteData.value.copy(
                        isApproved = true,
                        isDraft = false,
                        items = noteData.value.items.map { item ->
                            if (item.invoiceStatus == InvoiceStatus.DRAFT) {
                                item.copy(invoiceStatus = InvoiceStatus.READY)
                            } else {
                                item
                            }
                        }
                    )
                }
                saveChanges()
                navigate.invoke()
                hideLoadingOverlay()
            }
        } else {
            navigate.invoke()
        }
    }

    private suspend fun getNote() {
        if (noteId != null && noteId!!.isNotBlank()) {
            clientRepository.getNote(noteId!!)
                .onSuccess { note ->
                    clientRepository.getNoteItems(noteId!!, note.versionId)
                        .onSuccess { noteItems ->
                            val isValid = noteItems
                                .all { it.deleted || (it.description.isNotBlank() && it.minutes > 0) }
                                    && note.notes.isNotBlank()
                            noteData.value = noteData.value.copy(
                                notes = note.notes,
                                isDraft = note.isDraft,
                                isApproved = note.isApproved,
                                isValidEntry = isValid,
                                items = noteItems.map { it.mapToUiItem() }
                            )
                            hideLoadingOverlay()
                        }.onFailure {
                            showErrorMessageToast(it)
                            hideLoadingOverlay()
                        }
                }
                .onFailure {
                    showErrorMessageToast(it)
                    hideLoadingOverlay()
                }
        } else {
            hideLoadingOverlay()
        }
    }

    private suspend fun saveChanges() {
        if (hasUserChanges) {
            hasUserChanges = false
            with(noteData.value) {
                if (noteId == null) {
                    noteId = clientRepository.getNewNoteId(clientId)
                }
                val versionId = clientRepository.addNoteVersion(
                    clientId = clientId,
                    noteId = noteId!!,
                    notes = notes,
                    isDraft = isDraft,
                    isApproved = isApproved
                )
                clientRepository.addNoteItems(
                    noteId = noteId!!,
                    clientId = clientId,
                    versionId = versionId,
                    items = items.map { it.mapToDomain() }
                )
            }
        }
    }

    private fun EditNoteUiState.Item.mapToDomain(): ClientNoteItem =
        ClientNoteItem(
            type = type,
            start = Date.from(LocalDateTime.of(date, startTime).atZone(ZoneId.systemDefault()).toInstant()),
            minutes = minutes,
            description = description,
            invoiceStatus = invoiceStatus,
            billable = billable,
            deleted = deleted
        )

    private fun ClientNoteItem.mapToUiItem(): EditNoteUiState.Item {
        val startDateTime = Instant.ofEpochMilli(start.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        return EditNoteUiState.Item(
            type = type,
            date = startDateTime.toLocalDate(),
            startTime = startDateTime.toLocalTime(),
            minutes = minutes,
            description = description,
            invoiceStatus = if (invoiceStatus == InvoiceStatus.READY) InvoiceStatus.INVOICED else invoiceStatus,
            billable = billable,
            deleted = deleted
        )
    }
}