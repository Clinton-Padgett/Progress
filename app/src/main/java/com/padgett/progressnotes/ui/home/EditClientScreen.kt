package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.R
import com.padgett.progressnotes.ui.common.PrimaryTextButton
import com.padgett.progressnotes.ui.common.SecondaryTextButton
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme

@Composable
fun EditClientScreen(viewModel: EditClientViewModel, onBackClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState,
        onNameChanged = viewModel::onNameChanged,
        onReferenceChanged = viewModel::onReferenceChanged,
        onIsActiveChanged = viewModel::onIsActiveChanged,
        onSaveClicked = { viewModel.onSaveClicked(onBackClicked) },
        onCancelClicked = onBackClicked
    )
}

@Composable
private fun MainContent(
    uiState: EditClientUiState?,
    onNameChanged: (String) -> Unit,
    onReferenceChanged: (String) -> Unit,
    onIsActiveChanged: () -> Unit,
    onSaveClicked: () -> Unit,
    onCancelClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit customer details")

        OutlinedTextField(
            label = { Text("Name") },
            value = uiState?.name ?: "",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            onValueChange = onNameChanged
        )
        OutlinedTextField(
            label = { Text("Reference") },
            value = uiState?.reference ?: "",
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            onValueChange = onReferenceChanged
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Text(text = "Is active client", modifier = Modifier.weight(1F))
            IconButton(
                modifier = Modifier
                    .statusBarsPadding()
                    .clickable(onClick = onIsActiveChanged),
                onClick = onIsActiveChanged
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(
                        id = if (uiState?.isActive == true) R.drawable.ic_check_box_selected
                        else R.drawable.ic_check_box_unselected
                    ),
                    tint = Color.Unspecified,
                    contentDescription = ""
                )
            }
        }
        Spacer(modifier = Modifier.weight(1F))
        PrimaryTextButton(
            text = "Save",
            isEnabled = uiState != null && uiState.name.isNotBlank() && uiState.reference.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            onClick = onSaveClicked
        )
        SecondaryTextButton(
            text = "Discard changes", modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = onCancelClicked
        )
    }
}

@Preview
@Composable
private fun EditClientPreview() {
    ProgressNotesTheme {
        Surface {
            MainContent(
                uiState = null,
                onNameChanged = {},
                onReferenceChanged = {},
                onIsActiveChanged = {},
                onSaveClicked = {},
                onCancelClicked = {}
            )
        }
    }
}