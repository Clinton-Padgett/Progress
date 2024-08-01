package com.padgett.progressnotes

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.padgett.progressnotes.ui.BaseViewModel
import com.padgett.progressnotes.ui.UiEvent
import com.padgett.progressnotes.ui.authentication.PhoneNumberSignInScreen
import com.padgett.progressnotes.ui.authentication.PhoneNumberSignInViewModel
import com.padgett.progressnotes.ui.home.HomeScreen
import com.padgett.progressnotes.ui.home.HomeViewModel

data class NavCallbacks(
    val isLoadingOverlayVisible: (Boolean) -> Unit,
    val showSnackBar: (String) -> Unit,
    val showIndefiniteSnackBar: (String) -> Unit
)

@Composable
fun ProgressNavGraph(
    navController: NavHostController,
    navCallbacks: NavCallbacks
) {
    val context = LocalContext.current
    val navActions: ProgressNavigationActions = remember(navController) {
        ProgressNavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = ProgressDestinations.SIGN_IN_DESTINATION,
    ) {
        composable(ProgressDestinations.SIGN_IN_DESTINATION) {
            val viewModel = getViewModel<PhoneNumberSignInViewModel>(context = context, navCallbacks = navCallbacks)
            var hasValidSignIn by remember { mutableStateOf(viewModel.hasValidSignIn()) }
            if (!hasValidSignIn) {
                BackHandler(true) {
                    viewModel.onBackClicked()
                }
            }
            if (hasValidSignIn) {
                navActions.navigateToHome()
            } else {
                PhoneNumberSignInScreen(
                    viewModel = viewModel,
                    onSignInSuccess = { hasValidSignIn = true }
                )
            }
        }
        composable(ProgressDestinations.HOME_DESTINATION) {
            val viewModel = getViewModel<HomeViewModel>(context = context, navCallbacks = navCallbacks)
            BackHandler(true) {
                // Do nothing
            }
            HomeScreen(viewModel = viewModel, onSignedOut = navActions::navigateToSignIn)
        }
    }
}

@Composable
inline fun <reified VM : BaseViewModel> getViewModel(
    context: Context,
    navCallbacks: NavCallbacks,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(
        LocalViewModelStoreOwner.current
    ) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
): VM {
    val viewModel = hiltViewModel<VM>(viewModelStoreOwner).apply {
        setLoadingOverlayCallback(navCallbacks.isLoadingOverlayVisible)
        setUiEventCallback {
            when (it) {
                is UiEvent.ShowToast -> {
                    val length = if (it.longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
                    val text = if (it.extras.isEmpty()) {
                        context.getString(it.stringRes)
                    } else {
                        context.getString(it.stringRes, *it.extras.toTypedArray())
                    }
                    Toast.makeText(context, text, length).show()
                }
                is UiEvent.ShowSnackbar -> {
                    val text = if (it.extras.isEmpty()) {
                        context.getString(it.stringRes)
                    } else {
                        context.getString(it.stringRes, *it.extras.toTypedArray())
                    }
                    if (it.isIndefinite) {
                        navCallbacks.showIndefiniteSnackBar.invoke(text)
                    } else {
                        navCallbacks.showSnackBar.invoke(text)
                    }
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCallbacks()
        }
    }
    return viewModel
}
