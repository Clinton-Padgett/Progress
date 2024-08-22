package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme

@Composable
fun InvoicesScreen(viewModel: InvoicesViewModel) {
    MainContent()
}

@Composable
private fun MainContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(bottom = 80.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Invoices screen",
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewInvoicesScreen() {
    ProgressNotesTheme {
        MainContent()
    }
}