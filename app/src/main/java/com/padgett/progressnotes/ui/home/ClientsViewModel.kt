package com.padgett.progressnotes.ui.home

import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientsUiState(
    val clients: List<ClientDetails>
)

@HiltViewModel
class ClientsViewModel @Inject constructor(clientRepository: ClientRepository) : BaseViewModel() {
    private val clients = MutableStateFlow(ClientsUiState(listOf()))

    val uiState: StateFlow<ClientsUiState> =
        clients
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = ClientsUiState(listOf())
            )

    init {
        viewModelScope.launch {
            clientRepository.getClients().collect {
                clients.value = ClientsUiState(it.sortedWith(compareBy({ !it.isActive }, { it.name })))
            }
        }
    }
}