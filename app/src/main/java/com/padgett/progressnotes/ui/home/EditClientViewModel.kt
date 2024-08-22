package com.padgett.progressnotes.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.ProgressNavArgs
import com.padgett.progressnotes.R
import com.padgett.progressnotes.domain.ClientRepository
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditClientUiState(
    val reference: String,
    val name: String,
    val isActive: Boolean
)

@HiltViewModel
class EditClientViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle = SavedStateHandle(),
    private val clientRepository: ClientRepository
) : BaseViewModel() {
    private val clientData = MutableStateFlow<EditClientUiState?>(EditClientUiState("", "", true))

    private val clientId: String? = savedStateHandle[ProgressNavArgs.CLIENT_ID_ARG]

    val uiState: StateFlow<EditClientUiState?> =
        clientData
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )

    init {
        if (clientId != null) {
            viewModelScope.launch {
                showLoadingOverlay()
                clientRepository.getClient(clientId)
                    .onSuccess {
                        clientData.value = EditClientUiState(reference = it.reference, name = it.name, isActive = it.isActive)
                    }
                    .onFailure {
                        showErrorMessageToast(it)
                    }
                hideLoadingOverlay()
            }
        }
    }

    fun onNameChanged(name: String) {
        clientData.value = clientData.value?.copy(name = name)
    }

    fun onReferenceChanged(ref: String) {
        clientData.value = clientData.value?.copy(reference = ref)
    }

    fun onIsActiveChanged() {
        clientData.value = clientData.value?.copy(isActive = !clientData.value!!.isActive)
    }

    fun onSaveClicked(onSuccess: () -> Unit) {
        with(clientData.value!!) {
            if (name.isBlank() || reference.isBlank()) {
                showToast(R.string.edit_client_screen_error)
            } else {
                viewModelScope.launch {
                    if (clientId == null) {
                        clientRepository.addClient(name = name, reference = reference, isActive = isActive)
                    } else {
                        clientRepository.updateClient(id = clientId, name = name, reference = reference, isActive = isActive)
                    }
                    onSuccess.invoke()
                }
            }
        }
    }
}