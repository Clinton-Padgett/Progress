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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.padgett.progressnotes.ui.BaseViewModel
import com.padgett.progressnotes.ui.UiEvent
import com.padgett.progressnotes.ui.authentication.PhoneNumberSignInScreen
import com.padgett.progressnotes.ui.authentication.PhoneNumberSignInViewModel
import com.padgett.progressnotes.ui.home.AddNoteScreen
import com.padgett.progressnotes.ui.home.AddNoteViewModel
import com.padgett.progressnotes.ui.home.ClientsScreen
import com.padgett.progressnotes.ui.home.ClientsViewModel
import com.padgett.progressnotes.ui.home.EditClientScreen
import com.padgett.progressnotes.ui.home.EditClientViewModel
import com.padgett.progressnotes.ui.home.EditNoteScreen
import com.padgett.progressnotes.ui.home.EditNoteViewModel
import com.padgett.progressnotes.ui.home.InvoicePreviewScreen
import com.padgett.progressnotes.ui.home.InvoicePreviewViewModel
import com.padgett.progressnotes.ui.home.InvoicesScreen
import com.padgett.progressnotes.ui.home.InvoicesViewModel
import com.padgett.progressnotes.ui.home.QuickNoteScreen
import com.padgett.progressnotes.ui.home.QuickNoteViewModel

data class NavCallbacks(
    val isLoadingOverlayVisible: (Boolean) -> Unit,
    val showSnackBar: (String) -> Unit,
    val showIndefiniteSnackBar: (String) -> Unit
)

@Composable
fun ProgressNavGraph(
    navController: NavHostController,
    navCallbacks: NavCallbacks,
    navActions: ProgressNavigationActions
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = ProgressRoute.SIGN_IN,
    ) {
        composable(ProgressRoute.SIGN_IN) {
            val viewModel = getViewModel<PhoneNumberSignInViewModel>(context = context, navCallbacks = navCallbacks)
            var hasValidSignIn by remember { mutableStateOf(viewModel.hasValidSignIn()) }
            if (!hasValidSignIn) {
                BackHandler(true) {
                    viewModel.onBackClicked()
                }
            }
            if (hasValidSignIn) {
                navActions.navigateToQuickNote()
            } else {
                PhoneNumberSignInScreen(
                    viewModel = viewModel,
                    onSignInSuccess = { hasValidSignIn = true }
                )
            }
        }
        composable(ProgressRoute.QUICK_NOTE) {
            val viewModel = getViewModel<QuickNoteViewModel>(context = context, navCallbacks = navCallbacks)
            BackHandler(true) {
                // Do nothing
            }
            QuickNoteScreen(
                viewModel = viewModel,
                onNoteSelected = navActions::navigateToEditNote,
                onAddNoteClicked = navActions::navigateToAddNoteRoute,
                onSignedOut = navActions::navigateToSignIn
            )
        }
        composable(ProgressRoute.ADD_NOTE) {
            val viewModel = getViewModel<AddNoteViewModel>(context = context, navCallbacks = navCallbacks)
            AddNoteScreen(
                viewModel = viewModel,
                navigateToEditNote = navActions::navigateToAddNote
            )
        }
        composable(ProgressRoute.CLIENTS) {
            val viewModel = getViewModel<ClientsViewModel>(context = context, navCallbacks = navCallbacks)
            BackHandler(true) {
                // Do nothing
            }
            ClientsScreen(
                viewModel = viewModel,
                navigateToAddClient = navActions::navigateToAddClient,
                navigateToEditClient = navActions::navigateToEditClient
            )
        }
        composable(ProgressRoute.INVOICES) {
            val viewModel = getViewModel<InvoicesViewModel>(context = context, navCallbacks = navCallbacks)
            BackHandler(true) {
                // Do nothing
            }
            InvoicesScreen(viewModel = viewModel, onInvoiceSelected = navActions::navigateToInvoicePreview)
        }
        composable(
            route = ProgressRoute.INVOICE_PREVIEW,
            arguments = listOf(
                navArgument(ProgressNavArgs.CLIENT_ID_ARG) { type = NavType.StringType }
            )
        ) {
            val viewModel = getViewModel<InvoicePreviewViewModel>(context = context, navCallbacks = navCallbacks)
            BackHandler(true) {
                // Do nothing
            }
            InvoicePreviewScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable(route = ProgressRoute.CLIENT_ADD) {
            val viewModel = getViewModel<EditClientViewModel>(context = context, navCallbacks = navCallbacks)
            BackHandler(true) {
                // Do nothing
            }
            EditClientScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable(
            route = ProgressRoute.CLIENT_EDIT,
            arguments = listOf(
                navArgument(ProgressNavArgs.CLIENT_ID_ARG) { type = NavType.StringType }
            )
        ) {
            val viewModel = getViewModel<EditClientViewModel>(context = context, navCallbacks = navCallbacks)
            BackHandler(true) {
                // Do nothing
            }
            EditClientScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
        }
        composable(
            route = ProgressRoute.NOTE_ADD,
            arguments = listOf(
                navArgument(ProgressNavArgs.CLIENT_ID_ARG) { type = NavType.StringType }
            )
        ) {
            val viewModel = getViewModel<EditNoteViewModel>(context = context, navCallbacks = navCallbacks)
            EditNoteScreen(viewModel = viewModel) {
                navController.popBackStack(ProgressRouteName.QUICK_NOTE, true)
            }
        }
        composable(
            route = ProgressRoute.NOTE_EDIT,
            arguments = listOf(
                navArgument(ProgressNavArgs.NOTE_ID_ARG) { type = NavType.StringType },
                navArgument(ProgressNavArgs.CLIENT_ID_ARG) { type = NavType.StringType }
            )
        ) {
            val viewModel = getViewModel<EditNoteViewModel>(context = context, navCallbacks = navCallbacks)
            EditNoteScreen(viewModel = viewModel) {
                navController.popBackStack()
            }
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
