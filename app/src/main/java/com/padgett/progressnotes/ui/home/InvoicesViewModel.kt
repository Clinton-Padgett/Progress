package com.padgett.progressnotes.ui.home

import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvoicesUiState(
    val invoices: List<InvoiceDetails> = listOf()
) {
    data class InvoiceDetails(
        val clientId: String,
        val clientName: String
    )
}

@HiltViewModel
class InvoicesViewModel @Inject constructor(private val clientRepository: ClientRepository) : BaseViewModel() {

    private val invoices = MutableStateFlow(InvoicesUiState())

    val uiState: StateFlow<InvoicesUiState> =
        invoices
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = InvoicesUiState()
            )

    init {
        viewModelScope.launch {
            combine(clientRepository.getClients(), clientRepository.getItemsReadyForInvoice()) { clients, items ->
                items.groupBy { it.first }
                    .keys
                    .map { clientId ->
                        InvoicesUiState.InvoiceDetails(
                            clientId = clientId,
                            clientName = clients.first { it.id == clientId }.name
                        )
                    }
            }.collect {
                invoices.value = InvoicesUiState(it)
            }
        }
    }
}