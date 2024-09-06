package com.padgett.progressnotes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.TransparentBlack
import com.padgett.progressnotes.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val backPressedDisabledCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProgressNotesTheme {
                val navController: NavHostController = rememberNavController()
                val isLoadingOverlayVisible = rememberSaveable { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val navActions: ProgressNavigationActions = remember(navController) {
                    ProgressNavigationActions(navController)
                }
                val navCallbacks = NavCallbacks(
                    isLoadingOverlayVisible = {
                        if (it) {
                            onBackPressedDispatcher.addCallback(backPressedDisabledCallback)
                        } else {
                            backPressedDisabledCallback.remove()
                        }
                        isLoadingOverlayVisible.value = it
                    },
                    showSnackBar = {
                        scope.launch {
                            snackbarHostState.showSnackbar(it)
                        }
                    },
                    showIndefiniteSnackBar = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = it,
                                withDismissAction = true,
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                    }
                )
                Scaffold(
                    bottomBar = {
                        val currentRoute = navBackStackEntry?.destination?.route
                        if (currentRoute in listOf(
                                ProgressRoute.QUICK_NOTE,
                                ProgressRoute.CLIENTS,
                                ProgressRoute.INVOICES,
                                ProgressRoute.SETTINGS
                            )
                        ) {
                            BottomNavBar(currentRoute = currentRoute!!, navActions = navActions)
                        }
                    },
                    snackbarHost = {
                        ProgressSnackBarHost(snackbarHostState)
                    }
                ) {
                    ProgressNavGraph(navController = navController, navCallbacks = navCallbacks, navActions = navActions)
                }
                if (isLoadingOverlayVisible.value) {
                    LoadingOverlay()
                }
            }
        }
    }

    @Composable
    private fun BottomNavBar(currentRoute: String, navActions: ProgressNavigationActions) {
        var selectedItem by remember {
            mutableIntStateOf(
                when (currentRoute) {
                    ProgressRoute.SETTINGS -> 3
                    ProgressRoute.INVOICES -> 2
                    ProgressRoute.CLIENTS -> 1
                    else -> 0
                }
            )
        }
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = R.drawable.ic_person_notes), contentDescription = "") },
                label = { Text("Notes") },
                selected = selectedItem == 0,
                onClick = {
                    selectedItem = 0
                    navActions.navigateToQuickNote()
                }
            )
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = R.drawable.ic_people), contentDescription = "") },
                label = { Text("Clients") },
                selected = selectedItem == 1,
                onClick = {
                    selectedItem = 1
                    navActions.navigateToClients()
                }
            )
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = R.drawable.ic_dollar), contentDescription = "") },
                label = { Text("Invoices") },
                selected = selectedItem == 2,
                onClick = {
                    selectedItem = 2
                    navActions.navigateToInvoices()
                }
            )
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = "") },
                label = { Text("Settings") },
                selected = selectedItem == 3,
                onClick = {
                    selectedItem = 3
                    navActions.navigateToSettings()
                }
            )
        }
    }

    @Composable
    private fun ProgressSnackBarHost(snackbarHostState: SnackbarHostState) {
        SnackbarHost(hostState = snackbarHostState) { snackbarData ->
            Snackbar(
                content = {
                    Text(text = snackbarData.visuals.message, style = Typography.bodyLarge)
                },
                dismissAction = {
                    IconButton(
                        onClick = { snackbarData.dismiss() },
                        content = { Icon(Icons.Filled.Close, contentDescription = "") }
                    )
                }
            )
        }
    }

    @Composable
    private fun LoadingOverlay() {
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TransparentBlack)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    // Do nothing
                },
            contentAlignment = Alignment.Center
        ) {
            var isProgressShown by remember { mutableStateOf(false) }
            LaunchedEffect(true) {
                delay(2000)
                isProgressShown = true
            }
            if (isProgressShown) {
                CircularProgressIndicator()
            }
        }
    }
}