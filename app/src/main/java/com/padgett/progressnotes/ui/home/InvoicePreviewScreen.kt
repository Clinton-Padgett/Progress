package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.R
import com.padgett.progressnotes.domain.clients.models.TimeType
import com.padgett.progressnotes.ui.common.PrimaryTextButton
import com.padgett.progressnotes.ui.common.SecondaryTextButton
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography
import java.text.DateFormat
import java.text.NumberFormat
import java.time.Duration
import java.time.LocalTime
import java.util.Date

@Composable
fun InvoicePreviewScreen(viewModel: InvoicePreviewViewModel, onBackClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState,
        onItemSelected = viewModel::onItemSelected,
        onCreateClicked = { viewModel.onCreateClicked(onBackClicked) },
        onBackClicked = onBackClicked
    )
}

@Composable
private fun MainContent(
    uiState: InvoicePreviewUiState?,
    onItemSelected: (id: String) -> Unit,
    onCreateClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(uiState?.name ?: "")
        LazyColumn(modifier = Modifier.padding(vertical = 24.dp)) {
            items(uiState?.items ?: listOf()) {
                InvoiceItem(data = it, onItemSelected = onItemSelected)
                HorizontalDivider()
            }
            item {
                InvoiceTotals(uiState?.totals ?: listOf())
                PrimaryTextButton(
                    text = "Create invoice",
                    isEnabled = uiState != null && uiState.items.any { it.enabled },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCreateClicked
                )
                SecondaryTextButton(
                    text = "Close", modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    onClick = onBackClicked
                )
            }
        }
    }
}

@Composable
private fun InvoiceItem(data: InvoicePreviewUiState.Item, onItemSelected: (id: String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { onItemSelected.invoke(data.id) }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(
                    id = if (data.enabled) R.drawable.ic_check_box_selected
                    else R.drawable.ic_check_box_unselected
                ),
                contentDescription = ""
            )
        }
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(vertical = 4.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier.padding(end = 24.dp),
                    text = data.type.mapToShortDisplay(),
                    style = Typography.bodySmall
                )
                Text(
                    modifier = Modifier.padding(end = 24.dp),
                    text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(data.start),
                    style = Typography.bodySmall
                )
                Text(
                    text = LocalTime.MIN.plus(Duration.ofMinutes(data.minutes)).toString(),
                    style = Typography.bodySmall
                )
            }
            Text(
                modifier = Modifier.padding(end = 12.dp, top = 4.dp),
                text = data.description,
                style = Typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = NumberFormat.getCurrencyInstance().format(data.totalPrice),
            style = Typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun InvoiceTotals(totals: List<InvoicePreviewUiState.Total>) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        totals.forEach { total ->
            Row {
                Text(
                    text = "${total.type.mapToShortDisplay()} - ${total.type.mapToDisplay()}",
                    style = Typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 4.dp)
                )
                Text(
                    text = LocalTime.MIN.plus(Duration.ofMinutes(total.minutes)).toString(),
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = NumberFormat.getCurrencyInstance().format(total.totalPrice),
                    style = Typography.bodyMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .weight(0.5F)
                )
            }
        }
        Text(
            text = "TOTAL PRICE   ${NumberFormat.getCurrencyInstance().format(totals.sumOf { it.totalPrice.toDouble() })}",
            style = Typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Preview
@Composable
private fun InvoicePreviewPreview() {
    ProgressNotesTheme {
        Surface {
            MainContent(
                uiState = InvoicePreviewUiState(
                    "Clinton Padgett",
                    items = listOf(
                        InvoicePreviewUiState.Item(
                            id = "",
                            type = TimeType.TRAVEL,
                            start = Date(),
                            minutes = 55,
                            description = "Did something good",
                            totalPrice = 13.45F,
                            enabled = true
                        ),
                        InvoicePreviewUiState.Item(
                            id = "",
                            type = TimeType.HOME_VISIT,
                            start = Date(),
                            minutes = 55,
                            description = "Did something good",
                            totalPrice = 123.45F,
                            enabled = true
                        ),
                        InvoicePreviewUiState.Item(
                            id = "",
                            type = TimeType.TRAVEL,
                            start = Date(),
                            minutes = 55,
                            description = "Did something good with very long text that should be only two lines at most then truncated",
                            totalPrice = 123.45F,
                            enabled = true
                        )
                    ),
                    totals = listOf(
                        InvoicePreviewUiState.Total(
                            type = TimeType.HOME_VISIT,
                            minutes = 234,
                            totalPrice = 124.56F
                        ),
                        InvoicePreviewUiState.Total(
                            type = TimeType.TRAVEL,
                            minutes = 234,
                            totalPrice = 234.56F
                        ),
                        InvoicePreviewUiState.Total(
                            type = TimeType.OFFICE,
                            minutes = 234,
                            totalPrice = 34.56F
                        ),
                        InvoicePreviewUiState.Total(
                            type = TimeType.COMMUNITY_VISIT,
                            minutes = 234,
                            totalPrice = 1234.56F
                        ),
                        InvoicePreviewUiState.Total(
                            type = TimeType.EQUIPMENT_PICK_UP,
                            minutes = 234,
                            totalPrice = 1234.56F
                        )
                    )
                ),
                onItemSelected = {},
                onCreateClicked = {},
                onBackClicked = {}
            )
        }
    }
}