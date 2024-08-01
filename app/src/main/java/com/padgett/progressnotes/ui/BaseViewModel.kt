package com.padgett.progressnotes.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.padgett.progressnotes.R
import com.padgett.progressnotes.domain.common.exceptions.InvalidNumberException
import com.padgett.progressnotes.domain.common.exceptions.TooManyRequestsException

sealed class UiEvent {
    data class ShowToast(@StringRes val stringRes: Int, val extras: List<String>, val longDuration: Boolean) : UiEvent()
    data class ShowSnackbar(@StringRes val stringRes: Int, val extras: List<String>, val isIndefinite: Boolean) : UiEvent()
}

abstract class BaseViewModel : ViewModel() {

    private var isLoadingOverlayVisible: ((Boolean) -> Unit)? = null
    private var showUiEventCallback: ((UiEvent) -> Unit)? = null

    fun setLoadingOverlayCallback(callback: (Boolean) -> Unit) {
        isLoadingOverlayVisible = callback
    }

    fun setUiEventCallback(callback: ((UiEvent) -> Unit)) {
        showUiEventCallback = callback
    }

    fun clearCallbacks() {
        isLoadingOverlayVisible = null
        showUiEventCallback = null
    }

    fun showToast(@StringRes stringRes: Int, extras: List<String> = listOf(), longDuration: Boolean = false) {
        showUiEventCallback?.invoke(UiEvent.ShowToast(stringRes, extras, longDuration))
    }

    fun showErrorMessageToast(exception: Throwable?) {
        when (exception) {
            is InvalidNumberException -> showToast(R.string.error_phone_number_invalid, longDuration = true)
            is TooManyRequestsException -> showToast(R.string.error_too_many_requests, longDuration = true)
            else -> showToast(R.string.error_generic, longDuration = true)
        }
    }

    fun showSnackbar(@StringRes stringRes: Int, extras: List<String> = listOf(), isIndefinite: Boolean = true) {
        showUiEventCallback?.invoke(UiEvent.ShowSnackbar(stringRes, extras, isIndefinite))
    }

    protected fun showLoadingOverlay() {
        isLoadingOverlayVisible?.invoke(true)
    }

    protected fun hideLoadingOverlay() {
        isLoadingOverlayVisible?.invoke(false)
    }
}