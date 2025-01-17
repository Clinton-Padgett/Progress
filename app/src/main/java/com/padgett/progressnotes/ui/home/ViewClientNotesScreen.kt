package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography
import java.text.DateFormat
import java.util.Date

@Composable
fun ViewClientNotesScreen(viewModel: ViewClientNotesViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(uiState = uiState)
}

@Composable
private fun MainContent(uiState: ViewClientNotesUiState?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Client Notes - ${uiState?.clientName}")
        LazyColumn {
            items(uiState?.notes ?: listOf()) {
                NoteDetails(it)
            }
        }
    }
}

@Composable
private fun NoteDetails(note: ViewClientNotesUiState.Note) {
    Column(Modifier.padding(vertical = 4.dp)) {
        if (note.isDraft) {
            Text(
                text = "DRAFT",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = Color.Red
            )
        }
        Text(
            text = DateFormat.getDateTimeInstance().format(note.date),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            style = Typography.bodySmall,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = if (note.notes.isBlank()) "(No notes to show)" else note.notes,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            style = Typography.bodySmall
        )
        HorizontalDivider()
    }
}

@Preview
@Composable
private fun ViewClientNotesScreenPreview() {
    ProgressNotesTheme {
        Surface {
            MainContent(
                uiState = ViewClientNotesUiState(
                    "Jim",
                    listOf(
                        ViewClientNotesUiState.Note(
                            date = Date(),
                            "Did something and did not do the other thing, but it was done, or it was not, I am unsure. But I do know I did what was needed.",
                            false
                        ),
                        ViewClientNotesUiState.Note(
                            date = Date(),
                            "Did something else.",
                            true
                        ),
                        ViewClientNotesUiState.Note(
                            date = Date(),
                            "Did something else.",
                            false
                        ),
                        ViewClientNotesUiState.Note(
                            date = Date(),
                            "Did something else.",
                            false
                        )
                    )
                ),
            )
        }
    }
}