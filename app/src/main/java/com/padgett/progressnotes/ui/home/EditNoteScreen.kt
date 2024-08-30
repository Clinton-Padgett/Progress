package com.padgett.progressnotes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padgett.progressnotes.R
import com.padgett.progressnotes.domain.clients.models.InvoiceStatus
import com.padgett.progressnotes.domain.clients.models.TimeType
import com.padgett.progressnotes.toEpochMillis
import com.padgett.progressnotes.ui.common.ConfirmPrompt
import com.padgett.progressnotes.ui.common.PrimaryTextButton
import com.padgett.progressnotes.ui.common.SecondaryTextButton
import com.padgett.progressnotes.ui.theme.Charcoal
import com.padgett.progressnotes.ui.theme.LightGrey
import com.padgett.progressnotes.ui.theme.Linen
import com.padgett.progressnotes.ui.theme.MidGrey
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

@Composable
fun EditNoteScreen(viewModel: EditNoteViewModel, onCloseClicked: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        onPauseOrDispose {
            viewModel.onPause()
        }
    }

    MainContent(
        uiState = uiState,
        onNotesChanged = viewModel::onNotesChanged,
        onAddItemClicked = viewModel::onAddItemClicked,
        onApproveClicked = { viewModel.onApproveClicked(onCloseClicked) },
        onTypeChanged = viewModel::onTypeChanged,
        onStartDateChanged = viewModel::onStartDateChanged,
        onStartTimeChanged = viewModel::onStartTimeChanged,
        onMinutesChanged = viewModel::onMinutesChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onInvoiceClicked = viewModel::onInvoiceClicked,
        onBillableClicked = viewModel::onBillableClicked,
        onDeleteItemClicked = viewModel::onDeleteItemClicked,
        onCloseClicked = { viewModel.onCloseClicked(onCloseClicked) }
    )
}

@Composable
private fun MainContent(
    uiState: EditNoteUiState,
    onNotesChanged: (String) -> Unit,
    onAddItemClicked: () -> Unit,
    onApproveClicked: () -> Unit,
    onTypeChanged: (index: Int, TimeType) -> Unit,
    onStartDateChanged: (index: Int, LocalDate) -> Unit,
    onStartTimeChanged: (index: Int, LocalTime) -> Unit,
    onMinutesChanged: (index: Int, Long) -> Unit,
    onDescriptionChanged: (index: Int, String) -> Unit,
    onInvoiceClicked: (index: Int) -> Unit,
    onBillableClicked: (index: Int) -> Unit,
    onDeleteItemClicked: (index: Int) -> Unit,
    onCloseClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(text = uiState.clientName, modifier = Modifier.align(Alignment.Center))
            IconButton(
                enabled = !uiState.isApproved,
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = onAddItemClicked
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = ""
                )
            }
        }
        LazyColumn {
            itemsIndexed(uiState.items) { index, data ->
                NoteItem(
                    data = data,
                    onTypeChanged = { onTypeChanged.invoke(index, it) },
                    onStartDateChanged = { onStartDateChanged.invoke(index, it) },
                    onStartTimeChanged = { onStartTimeChanged.invoke(index, it) },
                    onMinutesChanged = { onMinutesChanged.invoke(index, it) },
                    onDescriptionChanged = { onDescriptionChanged.invoke(index, it) },
                    onInvoiceClicked = { onInvoiceClicked.invoke(index) },
                    onBillableClicked = { onBillableClicked.invoke(index) },
                    onDeleteClicked = { onDeleteItemClicked.invoke(index) }
                )
            }
            item {
                OutlinedTextField(
                    label = { Text("Notes") },
                    value = uiState.notes,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    onValueChange = onNotesChanged
                )
                PrimaryTextButton(
                    text = "Approve",
                    isEnabled = uiState.isValidEntry,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onApproveClicked
                )
                if (uiState.isDraft) {
                    SecondaryTextButton(
                        text = "Close", modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        onClick = onCloseClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteItem(
    data: EditNoteUiState.Item,
    onTypeChanged: (TimeType) -> Unit,
    onStartDateChanged: (LocalDate) -> Unit,
    onStartTimeChanged: (LocalTime) -> Unit,
    onMinutesChanged: (Long) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onInvoiceClicked: () -> Unit,
    onBillableClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val enableEdit = data.invoiceStatus in listOf(InvoiceStatus.DO_NOT_INVOICE, InvoiceStatus.DRAFT)
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(Charcoal, RoundedCornerShape(8.dp))
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            TimeTypeDropDown(onTypeChanged) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 12.dp)
                        .align(Alignment.Bottom)
                        .clickable(enabled = enableEdit, onClick = it),
                    value = data.type.mapToDisplay(),
                    label = { Text(text = stringResource(id = R.string.edit_note_screen_type)) },
                    maxLines = 1,
                    enabled = false,
                    onValueChange = {}
                )
            }
            ConfirmPrompt(title = "Remove item?", text = "Are you sure?", onConfirmed = onDeleteClicked) {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = ""
                    )
                }
            }
        }
        DateTimeRow(
            date = data.date,
            isEnabled = enableEdit,
            startTime = data.startTime,
            minutes = data.minutes,
            onDateChanged = onStartDateChanged,
            onStartTimeChanged = onStartTimeChanged,
            onMinutesChanged = onMinutesChanged
        )
        Text(
            text = if (data.minutes <= 0L) {
                "NO TIME ALLOCATED"
            } else {
                "Total time: ${LocalTime.MIN.plus(Duration.ofMinutes(data.minutes))}"
            },
            color = if (data.minutes <= 0L) Color.Red else LightGrey,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            label = { Text(text = "Description") },
            value = data.description,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            onValueChange = onDescriptionChanged
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Text(text = "Show on invoice")
            IconButton(enabled = enableEdit, onClick = onInvoiceClicked) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(
                        id = if (data.invoiceStatus == InvoiceStatus.DO_NOT_INVOICE) R.drawable.ic_check_box_unselected
                        else R.drawable.ic_check_box_selected
                    ),
                    tint = LightGrey,
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            if (data.invoiceStatus != InvoiceStatus.DO_NOT_INVOICE) {
                Text(text = "Billable")
                IconButton(enabled = enableEdit, onClick = onBillableClicked) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(
                            id = if (data.billable) R.drawable.ic_check_box_selected
                            else R.drawable.ic_check_box_unselected
                        ),
                        tint = LightGrey,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
private fun DateTimeRow(
    isEnabled: Boolean,
    date: LocalDate,
    startTime: LocalTime,
    minutes: Long,
    onDateChanged: (LocalDate) -> Unit,
    onStartTimeChanged: (LocalTime) -> Unit,
    onMinutesChanged: (minutes: Long) -> Unit
) {
    var isDatePickerShown by remember { mutableStateOf(false) }
    var isStartTimePickerShown by remember { mutableStateOf(false) }
    var isEndTimePickerShown by remember { mutableStateOf(false) }
    val endTime = if (minutes > 0) startTime.plusMinutes(minutes) else null
    Row(
        Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(0.6F)
                .align(Alignment.Bottom)
                .clickable(enabled = isEnabled, onClick = { isDatePickerShown = true }),
            value = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
            label = { Text(text = stringResource(id = R.string.edit_note_screen_date)) },
            maxLines = 1,
            enabled = false,
            onValueChange = {}
        )
        OutlinedTextField(
            modifier = Modifier
                .weight(0.5F)
                .align(Alignment.Bottom)
                .clickable(enabled = isEnabled, onClick = { isStartTimePickerShown = true })
                .padding(start = 4.dp),
            maxLines = 1,
            enabled = false,
            value = startTime.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "",
            label = { Text(text = stringResource(id = R.string.edit_note_screen_start)) },
            onValueChange = {}
        )
        OutlinedTextField(
            modifier = Modifier
                .weight(0.5F)
                .align(Alignment.Bottom)
                .clickable(enabled = isEnabled, onClick = { isEndTimePickerShown = true })
                .padding(start = 4.dp),
            maxLines = 1,
            enabled = false,
            value = endTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "",
            label = { Text(text = stringResource(id = R.string.edit_note_screen_end)) },
            onValueChange = {}
        )
    }
    if (isDatePickerShown) {
        DatePickerModal(
            date = date,
            onDateSelected = {
                onDateChanged.invoke(it)
                isDatePickerShown = false
            },
            onDismiss = { isDatePickerShown = false }
        )
    }
    if (isStartTimePickerShown || isEndTimePickerShown) {
        TimePickerModel(
            time = if (isStartTimePickerShown) startTime else endTime ?: startTime,
            onTimeSelected = {
                if (isStartTimePickerShown) {
                    onStartTimeChanged.invoke(it)
                    isStartTimePickerShown = false
                } else {
                    val selectedMinutes = startTime.until(it, ChronoUnit.MINUTES) + 1
                    if (isEndTimePickerShown && selectedMinutes > 0L) {
                        onMinutesChanged.invoke(selectedMinutes)
                        isEndTimePickerShown = false
                    }
                }
            },
            onDismiss = {
                isStartTimePickerShown = false
                isEndTimePickerShown = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date.toEpochMillis())

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val dateSelected = LocalDate.from(Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(ZoneId.systemDefault()))
                    onDateSelected.invoke(dateSelected)
                    onDismiss()
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(
                    text = stringResource(id = android.R.string.ok),
                    color = if (datePickerState.selectedDateMillis != null) Linen else MidGrey,
                    style = Typography.titleMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel), style = Typography.titleMedium)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerModel(
    time: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = time?.hour ?: 12,
        initialMinute = time?.minute ?: 0,
        is24Hour = false
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    onTimeSelected.invoke(selectedTime)
                    onDismiss.invoke()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.ok),
                    style = Typography.titleMedium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel), style = Typography.titleMedium)
            }
        }
    ) {
        TimePicker(
            state = timePickerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}

@Composable
fun TimeTypeDropDown(onSelected: (TimeType) -> Unit, content: @Composable (show: () -> Unit) -> Unit) {
    var isShown by remember { mutableStateOf(false) }
    content {
        isShown = true
    }
    if (isShown) {
        DropdownMenu(
            expanded = isShown,
            modifier = Modifier.fillMaxWidth(0.9F),
            onDismissRequest = { isShown = false }
        ) {
            TimeType.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(text = it.mapToDisplay(), style = Typography.titleMedium)
                    },
                    onClick = {
                        isShown = false
                        onSelected.invoke(it)
                    })
            }
        }
    }
}

@Preview
@Composable
private fun EditClientPreview() {
    ProgressNotesTheme {
        Surface {
            MainContent(
                uiState = EditNoteUiState(
                    clientName = " Clinton Padgett",
                    items = listOf(
                        EditNoteUiState.Item(
                            type = TimeType.HOME_VISIT,
                            date = LocalDate.now().minusDays(1L),
                            minutes = 0L,
                            startTime = LocalTime.now(),
                            description = "Did some home visit"
                        ),
                        EditNoteUiState.Item(
                            type = TimeType.TRAVEL,
                            date = LocalDate.now(),
                            startTime = LocalTime.now(),
                            minutes = 0L,
                            description = "Travel time"
                        )
                    )
                ),
                onNotesChanged = {},
                onAddItemClicked = {},
                onApproveClicked = {},
                onTypeChanged = { _, _ -> },
                onStartTimeChanged = { _, _ -> },
                onStartDateChanged = { _, _ -> },
                onMinutesChanged = { _, _ -> },
                onDescriptionChanged = { _, _ -> },
                onInvoiceClicked = {},
                onBillableClicked = {},
                onDeleteItemClicked = {},
                onCloseClicked = {}
            )
        }
    }
}