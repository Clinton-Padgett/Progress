package com.padgett.progressnotes.ui.home

import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.domain.AuthenticationRepository
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class QuickNoteUiState(
    val notes: List<NoteDetails> = listOf()
) {
    data class NoteDetails(
        val clientId: String,
        val noteId: String,
        val created: Date,
        val clientName: String,
        val notes: String
    )
}

@HiltViewModel
class QuickNoteViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository,
    private val clientRepository: ClientRepository
) : BaseViewModel() {

    private val notes = MutableStateFlow(QuickNoteUiState())

    val uiState: StateFlow<QuickNoteUiState> =
        notes
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = QuickNoteUiState()
            )

    init {
        viewModelScope.launch {
            clientRepository.getDraftNotes()
                .combine(clientRepository.getClients()) { notes, clients ->
                    notes.mapNotNull { note ->
                        clients.firstOrNull { it.id == note.clientId }?.let { client ->
                            QuickNoteUiState.NoteDetails(
                                clientId = note.clientId,
                                noteId = note.noteId,
                                created = note.created,
                                clientName = client.name,
                                notes = note.notes
                            )
                        }
                    }
                }.collect {
                    notes.value = QuickNoteUiState(it)
                }
        }
    }

    fun onSignOutClicked() {
        authRepository.signOut()
    }
}