package com.padgett.progressnotes

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.padgett.progressnotes.ui.theme.Black
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.TransparentBlack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val backPressedDisabledCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProgressNotesTheme {
                val navController: NavHostController = rememberNavController()
                val isLoadingOverlayVisible = rememberSaveable { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
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
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .background(Black)) {
                    ProgressNavGraph(navController = navController, navCallbacks)
                }
                if (isLoadingOverlayVisible.value) {
                    LoadingOverlay()
                }
            }
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