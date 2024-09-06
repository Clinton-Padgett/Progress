package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.R
import com.padgett.progressnotes.domain.clients.models.ClientDetails
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography

@Composable
fun ClientsScreen(viewModel: ClientsViewModel, navigateToAddClient: () -> Unit, navigateToEditClient: (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState,
        onAddClientClicked = navigateToAddClient,
        onClientSelected = viewModel::onClientSelected,
        onEditClicked = navigateToEditClient
    )
}

@Composable
private fun MainContent(
    uiState: ClientsUiState,
    onAddClientClicked: () -> Unit,
    onClientSelected: (id: String) -> Unit,
    onEditClicked: (id: String) -> Unit
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
                text = "Clients",
                style = Typography.headlineSmall,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterEnd),
                onClick = onAddClientClicked
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_add),
                    contentDescription = ""
                )
            }
        }
        LazyColumn {
            items(uiState.clients) {
                ClientCard(data = it, onClientSelected = onClientSelected, onEditClicked = onEditClicked)
            }
        }
    }
}

@Composable
private fun ClientCard(data: ClientDetails, onClientSelected: (id: String) -> Unit, onEditClicked: (id: String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .clickable { onClientSelected.invoke(data.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(12.dp)
        ) {
            Text(
                text = data.name,
                style = Typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = data.reference,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = Typography.bodySmall
            )
        }
        if (!data.isActive) {
            Text(text = "Inactive", style = Typography.bodyLarge, color = MaterialTheme.colorScheme.error)
        }
        IconButton(
            modifier = Modifier
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.CircleShape),
            onClick = { onEditClicked.invoke(data.id) }
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = ""
            )
        }
    }
}

@Preview
@Composable
private fun PreviewClientsScreen() {
    ProgressNotesTheme {
        MainContent(
            uiState = ClientsUiState(
                listOf(
                    ClientDetails(
                        id = "",
                        name = "Clinton",
                        reference = "REF1",
                        isActive = true
                    ),
                    ClientDetails(
                        id = "",
                        name = "Alex",
                        reference = "REF2",
                        isActive = false
                    )
                )
            ),
            onAddClientClicked = {},
            onClientSelected = {},
            onEditClicked = {}
        )
    }
}