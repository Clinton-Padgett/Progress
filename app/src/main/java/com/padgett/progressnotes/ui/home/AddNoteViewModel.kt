package com.padgett.progressnotes.ui.home

import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddNoteUiState(
    val clients: List<Client>
) {
    data class Client(
        val id: String,
        val name: String
    )
}

@HiltViewModel
class AddNoteViewModel @Inject constructor(private val clientRepository: ClientRepository) : BaseViewModel() {
    private val clients = MutableStateFlow(AddNoteUiState(listOf()))

    val uiState: StateFlow<AddNoteUiState> =
        clients
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = AddNoteUiState(listOf())
            )

    init {
        viewModelScope.launch {
            clientRepository.getClients().first().let {
                clients.value = AddNoteUiState(it.sortedBy { it.name }.map { AddNoteUiState.Client(it.id, it.name) })
            }
        }
    }

    fun onClientSelected(clientId: String, navigateToEditNote: (clientId: String) -> Unit) {
        showLoadingOverlay()
        viewModelScope.launch {
            navigateToEditNote.invoke(clientId)
            hideLoadingOverlay()
        }
    }
}