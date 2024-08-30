package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.ui.theme.Charcoal
import com.padgett.progressnotes.ui.theme.LightGrey
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography

@Composable
fun InvoicesScreen(
    viewModel: InvoicesViewModel,
    onInvoiceSelected: (clientId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState,
        onInvoiceSelected = onInvoiceSelected
    )
}

@Composable
private fun MainContent(
    uiState: InvoicesUiState,
    onInvoiceSelected: (clientId: String) -> Unit
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
                text = "Draft notes",
                style = Typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
        }
        LazyColumn {
            items(uiState.invoices) {
                InvoiceCard(data = it, onClicked = onInvoiceSelected)
            }
        }
    }
}

@Composable
private fun InvoiceCard(data: InvoicesUiState.InvoiceDetails, onClicked: (clientId: String) -> Unit) {
    Text(
        text = data.clientName,
        style = Typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, LightGrey, RoundedCornerShape(8.dp))
            .background(Charcoal)
            .clickable { onClicked.invoke(data.clientName) }
            .padding(horizontal = 12.dp, vertical = 20.dp)
    )
}

@Preview
@Composable
private fun PreviewInvoicesScreen() {
    ProgressNotesTheme {
        MainContent(
            uiState = InvoicesUiState(
                listOf(
                    InvoicesUiState.InvoiceDetails(
                        clientId = "1",
                        clientName = "Joe Blow",
                    ),
                    InvoicesUiState.InvoiceDetails(
                        clientId = "2",
                        clientName = "Clinton Padgett",
                    ),
                    InvoicesUiState.InvoiceDetails(
                        clientId = "3",
                        clientName = "Alex Padgett",
                    )
                )
            ),
            onInvoiceSelected = {}
        )
    }
}