package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.padgett.progressnotes.R
import com.padgett.progressnotes.ui.common.RoundedTextButton
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme

@Composable
fun HomeScreen(viewModel: HomeViewModel, onSignedOut: () -> Unit) {
    MainContent(onSignOutClicked = {
        viewModel.onSignOutClicked()
        onSignedOut.invoke()
    })
}

@Composable
private fun MainContent(onSignOutClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Home screen",
                modifier = Modifier
                    .align(Alignment.Center)
            )
            RoundedTextButton(
                text = stringResource(id = R.string.home_screen_sign_out),
                onClick = onSignOutClicked,
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewMainContent() {
    ProgressNotesTheme {
        MainContent {}
    }
}