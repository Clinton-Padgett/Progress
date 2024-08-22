package com.padgett.progressnotes.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.padgett.progressnotes.ui.theme.MidGrey
import com.padgett.progressnotes.ui.theme.Purple80
import com.padgett.progressnotes.ui.theme.Typography

@Composable
fun ConfirmPrompt(title: String, text: String, onConfirmed: () -> Unit, content: @Composable (show: () -> Unit) -> Unit) {
    var isDialogShown by remember { mutableStateOf(false) }
    content {
        isDialogShown = true
    }
    if (isDialogShown) {
        AlertDialog(
            title = {
                Text(title)
            },
            text = {
                Text(text)
            },
            onDismissRequest = {
                isDialogShown = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmed.invoke()
                        isDialogShown = false
                    },
                    border = BorderStroke(1.dp, MidGrey),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(text = "Yes", color = Purple80, style = Typography.titleMedium)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isDialogShown = false
                    },
                    border = BorderStroke(1.dp, MidGrey),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(text = "Cancel", color = Purple80, style = Typography.titleMedium)
                }
            }
        )
    }
}