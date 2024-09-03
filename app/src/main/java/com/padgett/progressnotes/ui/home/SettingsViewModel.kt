package com.padgett.progressnotes.ui.home

import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.domain.AuthenticationRepository
import com.padgett.progressnotes.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isSignedIntoXero: Boolean? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) : BaseViewModel() {

    private val isSignedIntoXero = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<SettingsUiState> =
        isSignedIntoXero.map {
            SettingsUiState(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SettingsUiState()
        )

    init {
        viewModelScope.launch {
            authenticationRepository.isXeroAuthenticated().collect {
                isSignedIntoXero.value = it
            }
        }
    }

    fun onSignOutClicked() {
        authenticationRepository.signOut()
    }

    fun onDisconnectXeroClicked() {

    }
}