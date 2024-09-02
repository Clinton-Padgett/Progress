package com.padgett.progressnotes.ui.home

import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.domain.clients.models.InvoiceStatus
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
        val clientName: String,
        val startDate: Long,
        val endDate: Long,
        val totalPrice: Float
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
                items.groupBy { it.clientId }
                    .map { item ->
                        InvoicesUiState.InvoiceDetails(
                            clientId = item.key,
                            clientName = clients.first { it.id == item.key }.name,
                            startDate = item.value.minOf { it.start.time },
                            endDate = item.value.maxOf { it.start.time },
                            totalPrice = (
                                    item.value.filter { it.billable && it.invoiceStatus == InvoiceStatus.READY && !it.deleted }
                                        .sumOf { it.minutes } / 60) * clientRepository.pricePerHour
                        )
                    }
            }.collect {
                invoices.value = InvoicesUiState(it)
            }
        }
    }
}