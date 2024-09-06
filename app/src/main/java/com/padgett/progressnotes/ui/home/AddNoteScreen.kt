package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun AddNoteScreen(viewModel: AddNoteViewModel, navigateToEditNote: (clientId: String) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState,
        onClientSelected = { viewModel.onClientSelected(it, navigateToEditNote) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    uiState: AddNoteUiState,
    onClientSelected: (clientId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(bottom = 80.dp)
    ) {
        var selectedClient by remember { mutableStateOf<AddNoteUiState.Client?>(null) }
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        Text(
            text = "Select client",
            style = Typography.headlineSmall,
            modifier = Modifier
                .padding(16.dp)
        )
        LazyColumn {
            items(uiState.clients) {
                ClientCard(data = it, onClientSelected = {
                    selectedClient = it
                })
            }
        }
        if (selectedClient != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    selectedClient = null
                },
                sheetState = sheetState
            ) {
                Text(
                    text = "Add new note for ${selectedClient?.name}",
                    style = Typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                HorizontalDivider()
                Text(
                    text = "Yes",
                    style = Typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        selectedClient?.let { onClientSelected.invoke(it.id) }
                                        selectedClient = null
                                    }
                                }
                        }
                        .padding(16.dp)
                )
                HorizontalDivider()
                Text(
                    text = "Cancel",
                    style = Typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        selectedClient = null
                                    }
                                }
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ClientCard(data: AddNoteUiState.Client, onClientSelected: (AddNoteUiState.Client) -> Unit) {
    Text(
        text = data.name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .clickable { onClientSelected.invoke(data) }
            .padding(20.dp),
        style = Typography.titleLarge,
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Preview
@Composable
private fun PreviewAddNoteScreen() {
    ProgressNotesTheme {
        MainContent(
            uiState = AddNoteUiState(
                listOf(
                    AddNoteUiState.Client(
                        id = "",
                        name = "Clinton"
                    ),
                    AddNoteUiState.Client(
                        id = "",
                        name = "Alex"
                    )
                )
            ),
            onClientSelected = {}
        )
    }
}