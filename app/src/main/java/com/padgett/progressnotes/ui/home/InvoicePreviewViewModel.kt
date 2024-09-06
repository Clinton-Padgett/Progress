package com.padgett.progressnotes.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.ProgressNavArgs
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.domain.clients.models.TimeType
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class InvoicePreviewUiState(
    val name: String = "",
    val items: List<Item> = listOf(),
    val totals: List<Total> = listOf()
) {
    data class Item(
        val id: String,
        val type: TimeType,
        val start: Date,
        val minutes: Long = 0,
        val description: String,
        val totalPrice: Float,
        val enabled: Boolean
    )

    data class Total(
        val type: TimeType,
        val minutes: Long,
        val totalPrice: Float
    )
}

@HiltViewModel
class InvoicePreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle = SavedStateHandle(),
    private val clientRepository: ClientRepository
) : BaseViewModel() {
    private val invoiceData = MutableStateFlow<List<InvoicePreviewUiState.Item>>(listOf())
    private val clientName = MutableStateFlow("")

    private val clientId: String = savedStateHandle[ProgressNavArgs.CLIENT_ID_ARG]!!

    val uiState: StateFlow<InvoicePreviewUiState?> =
        invoiceData.combine(clientName) { items, name ->
            InvoicePreviewUiState(
                name = name,
                items = items,
                totals = mapTotals(items)
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )

    init {
        viewModelScope.launch {
            showLoadingOverlay()
            clientRepository.getClient(clientId)
                .onSuccess {
                    clientName.value = it.name
                }
                .onFailure {
                    showErrorMessageToast(it)
                }
            hideLoadingOverlay()
        }
        viewModelScope.launch {
            clientRepository.getItemsReadyForInvoice(clientId).first().let {
                invoiceData.value =
                    it.filter { !it.deleted }
                        .sortedBy { it.start.time }
                        .map { item ->
                            InvoicePreviewUiState.Item(
                                id = item.id!!,
                                type = item.type,
                                start = item.start,
                                minutes = item.minutes,
                                description = item.description,
                                totalPrice = if (item.billable) (item.minutes / 60F) * clientRepository.pricePerHour else 0F,
                                enabled = true
                            )
                        }
            }
        }
    }

    fun onItemSelected(id: String) {
        invoiceData.value = invoiceData.value.map { if (it.id == id) it.copy(enabled = !it.enabled) else it }
    }

    fun onCreateClicked(navigateBack: () -> Unit) {
        navigateBack.invoke()
    }

    private fun mapTotals(items: List<InvoicePreviewUiState.Item>): List<InvoicePreviewUiState.Total> =
        items.groupBy { it.type }
            .map {
                val minutes = it.value.filter { it.enabled }.sumOf { it.minutes }
                val totalPrice = it.value.filter { it.enabled }.sumOf { it.totalPrice.toDouble() }
                InvoicePreviewUiState.Total(
                    type = it.key,
                    minutes = minutes,
                    totalPrice = totalPrice.toFloat()
                )
            }
}