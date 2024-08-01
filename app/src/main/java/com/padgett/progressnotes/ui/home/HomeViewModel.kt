package com.padgett.progressnotes.ui.home

import androidx.lifecycle.viewModelScope
import com.padgett.progressnotes.R
import com.padgett.progressnotes.domain.AuthenticationRepository
import com.padgett.progressnotes.domain.common.models.StringResource
import com.padgett.progressnotes.ui.BaseViewModel
import com.padgett.progressnotes.ui.authentication.PhoneNumberSignInUiViewState.DisplayMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : BaseViewModel() {

    fun onSignOutClicked() {
        authRepository.signOut()
    }
}