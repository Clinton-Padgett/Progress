package com.padgett.progressnotes.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.ProgressNavArgs
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ViewClientNotesUiState(
    val clientName: String,
    val notes: List<Note>
) {
    data class Note(
        val date: Date,
        val notes: String,
        val isDraft: Boolean
    )
}

@HiltViewModel
class ViewClientNotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle = SavedStateHandle(),
    clientRepository: ClientRepository
) : BaseViewModel() {
    private val viewState = MutableStateFlow(ViewClientNotesUiState("", listOf()))
    private val clientId: String = savedStateHandle[ProgressNavArgs.CLIENT_ID_ARG]!!

    val uiState: StateFlow<ViewClientNotesUiState> =
        viewState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = ViewClientNotesUiState("", listOf())
            )

    init {
        viewModelScope.launch {
            clientRepository.getClient(clientId).onSuccess {
                viewState.value = viewState.value.copy(clientName = it.name)
            }
            clientRepository.getNotesForClient(clientId).collect {
                viewState.value = viewState.value.copy(
                    notes = it.sortedByDescending { it.created }
                        .map {
                            ViewClientNotesUiState.Note(
                                date = it.created,
                                notes = it.notes,
                                isDraft = it.isDraft
                            )
                        }
                )
            }
        }
    }
}