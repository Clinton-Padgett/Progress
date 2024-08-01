package com.padgett.progressnotes.ui.authentication

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

data class PhoneNumberSignInUiViewState(
    val title: StringResource = StringResource(R.string.phone_sign_in_screen_title),
    val message: StringResource = StringResource(R.string.phone_sign_in_screen_message),
    val countryCode: String = "",
    val phoneNumber: String = "",
    val timeOut: Int = 0,
    val displayMode: DisplayMode = DisplayMode.NUMBER,
    val code: String = ""
) {
    enum class DisplayMode {
        NUMBER, CODE
    }
}

@HiltViewModel
class PhoneNumberSignInViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : BaseViewModel() {

    companion object {
        const val CODE_LENGTH = 6
        const val MAX_NAME_LENGTH = 30
    }

    private val viewState = MutableStateFlow(PhoneNumberSignInUiViewState())

    val uiState: StateFlow<PhoneNumberSignInUiViewState> =
        viewState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = PhoneNumberSignInUiViewState()
            )

    init {
        viewModelScope.launch {
            authRepository.retryPhoneVerificationTimer()
                .collect {
                    viewState.value = viewState.value.copy(timeOut = it)
                }
        }
    }

    fun hasValidSignIn(): Boolean = authRepository.isSignedIn()

    fun onCountryCodeChanged(number: String) {
        viewState.value = viewState.value.copy(countryCode = number)
    }

    fun onPhoneNumberChanged(number: String) {
        if (number.startsWith("0")) return
        viewState.value = viewState.value.copy(phoneNumber = number)
    }

    fun onCodeChanged(number: String) {
        if (number.length > CODE_LENGTH || (number.isNotBlank() && number.toIntOrNull() == null)) return
        viewState.value = viewState.value.copy(code = number)
    }

    fun onContinueClicked(onSignInCompleted: () -> Unit) {
        viewModelScope.launch {
            showLoadingOverlay()
            val code = viewState.value.code
            val phoneNumber = viewState.value.countryCode + viewState.value.phoneNumber
            when {
                viewState.value.displayMode == DisplayMode.NUMBER -> {
                    val result = authRepository.verifyPhoneNumber(phoneNumber)
                    when {
                        result.getOrNull() == true -> onSignInCompleted.invoke()
                        result.getOrNull() == false -> requestCode()
                        else -> showErrorMessageToast(result.exceptionOrNull())
                    }
                }
                viewState.value.displayMode == DisplayMode.CODE && code.length == CODE_LENGTH -> {
                    val result = authRepository.verifyCode(code)
                    when {
                        result.isSuccess -> onSignInCompleted.invoke()
                        result.isFailure -> showErrorMessageToast(result.exceptionOrNull())
                    }
                }
            }
            hideLoadingOverlay()
        }
    }

    fun onBackClicked() {
        when (uiState.value.displayMode) {
            DisplayMode.NUMBER -> Unit
            DisplayMode.CODE -> {
                viewState.value = viewState.value.copy(
                    displayMode = DisplayMode.NUMBER,
                    title = StringResource(R.string.phone_sign_in_screen_title),
                    message = StringResource(R.string.phone_sign_in_screen_message),
                )
            }
        }
    }

    private fun requestCode() {
        viewState.value = viewState.value.copy(
            title = StringResource(R.string.phone_sign_in_screen_code_title),
            message = StringResource(R.string.phone_sign_in_screen_code_message, listOf(viewState.value.countryCode + viewState.value.phoneNumber)),
            displayMode = DisplayMode.CODE,
            code = ""
        )
    }
}