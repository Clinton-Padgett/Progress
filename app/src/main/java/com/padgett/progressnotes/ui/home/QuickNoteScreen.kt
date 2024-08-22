package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.R
import com.padgett.progressnotes.ui.common.ConfirmPrompt
import com.padgett.progressnotes.ui.common.RoundedTextButton
import com.padgett.progressnotes.ui.theme.Charcoal
import com.padgett.progressnotes.ui.theme.LightGrey
import com.padgett.progressnotes.ui.theme.Linen
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun QuickNoteScreen(viewModel: QuickNoteViewModel, onNoteSelected: (id: String) -> Unit, onAddNoteClicked: () -> Unit, onSignedOut: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState,
        onNoteSelected = onNoteSelected,
        onAddNoteClicked = onAddNoteClicked,
        onSignOutClicked = {
            viewModel.onSignOutClicked()
            onSignedOut.invoke()
        })
}

@Composable
private fun MainContent(uiState: QuickNoteUiState, onNoteSelected: (id: String) -> Unit, onAddNoteClicked: () -> Unit, onSignOutClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(bottom = 80.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Draft notes",
                style = Typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
            ConfirmPrompt(title = "Sign out?", text = "Are you sure?", onConfirmed = onSignOutClicked) {
                RoundedTextButton(
                    text = stringResource(id = R.string.home_screen_sign_out),
                    onClick = it,
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                )
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp),
                onClick = onAddNoteClicked
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_note),
                    tint = Linen,
                    contentDescription = ""
                )
            }
        }
        LazyColumn {
            items(uiState.notes) {
                NoteCard(data = it, onClicked = onNoteSelected)
            }
        }
    }
}

@Composable
private fun NoteCard(data: QuickNoteUiState.NoteDetails, onClicked: (id: String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, LightGrey, RoundedCornerShape(8.dp))
            .background(Charcoal)
            .clickable { onClicked.invoke(data.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(12.dp)
        ) {
            Row {
                Text(
                    text = data.clientName,
                    style = Typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .weight(1F)
                )
                Text(
                    text = SimpleDateFormat.getDateInstance().format(data.created),
                    style = Typography.bodySmall,
                )
            }

            Text(
                text = data.notes,
                style = Typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun PreviewQuickNoteScreen() {
    ProgressNotesTheme {
        MainContent(
            uiState = QuickNoteUiState(
                listOf(
                    QuickNoteUiState.NoteDetails(
                        id = "",
                        created = Date(),
                        clientName = "Joe Blow",
                        notes = "Some long notes here which we need to truncate if they get too long to display in a nice way. This should not be any longer than 2 lines."
                    ),
                    QuickNoteUiState.NoteDetails(
                        id = "",
                        created = Date(),
                        clientName = "Clinton Padgett",
                        notes = "Who is he?"
                    ),
                    QuickNoteUiState.NoteDetails(
                        id = "",
                        created = Date(),
                        clientName = "Alex Padgett",
                        notes = "Yes please"
                    )
                )
            ),
            onNoteSelected = {},
            onAddNoteClicked = {},
            onSignOutClicked = {}
        )
    }
}