package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.R
import com.padgett.progressnotes.ui.common.ConfirmPrompt
import com.padgett.progressnotes.ui.common.SecondaryTextButton
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onSignedOut: () -> Unit,
    onXeroSignInClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState,
        onSignOutClicked = {
            viewModel.onSignOutClicked()
            onSignedOut.invoke()
        },
        onXeroSignOutClicked = viewModel::onDisconnectXeroClicked,
        onXeroSignInClicked = onXeroSignInClicked
    )
}

@Composable
private fun MainContent(
    uiState: SettingsUiState,
    onSignOutClicked: () -> Unit,
    onXeroSignOutClicked: () -> Unit,
    onXeroSignInClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(bottom = 80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Text(
                text = "Settings",
                style = Typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
        }
        ConfirmPrompt(title = "Sign out?", text = "Are you sure?", onConfirmed = onSignOutClicked) {
            SecondaryTextButton(
                text = stringResource(id = R.string.settings_screen_sign_out),
                onClick = it,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        if (uiState.isSignedIntoXero == true) {
            ConfirmPrompt(title = "Disconnect Xero?", text = "Invoicing will not work, are you sure?", onConfirmed = onXeroSignOutClicked) {
                SecondaryTextButton(
                    text = stringResource(id = R.string.settings_screen_xero_disconnect),
                    onClick = it,
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
        if (uiState.isSignedIntoXero == false) {
            SecondaryTextButton(
                text = stringResource(id = R.string.settings_screen_xero_connect),
                onClick = onXeroSignInClicked,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
    ProgressNotesTheme {
        MainContent(
            uiState = SettingsUiState(
                isSignedIntoXero = false
            ),
            onSignOutClicked = {},
            onXeroSignInClicked = {},
            onXeroSignOutClicked = {}
        )
    }
}