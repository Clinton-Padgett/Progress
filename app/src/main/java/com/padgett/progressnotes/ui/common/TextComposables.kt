package com.padgett.progressnotes.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.padgett.progressnotes.ui.theme.Linen
import com.padgett.progressnotes.ui.theme.MidGrey
import com.padgett.progressnotes.ui.theme.Typography
import kotlinx.coroutines.delay

/***
 * Fades the text view into view, shows for 2 seconds then fades back out repeating for
 * each string resource and then repeats indefinitely.
 */
@Composable
fun ScrollingTextView(modifier: Modifier = Modifier, textResIds: List<Int>) {
    val view = LocalView.current
    var loadingMessageIndex by remember { mutableIntStateOf(0) }
    val loadingMessageAlpha = remember { Animatable(if (view.isInEditMode) 1F else 0f) }
    LaunchedEffect(key1 = loadingMessageIndex) {
        loadingMessageAlpha.animateTo(targetValue = 1F, animationSpec = tween(1000))
        delay(2000)
        loadingMessageAlpha.animateTo(targetValue = 0F, animationSpec = tween(1000))
        if (loadingMessageIndex == textResIds.lastIndex) {
            loadingMessageIndex = 0
        } else {
            loadingMessageIndex++
        }
    }
    Text(
        text = stringResource(id = textResIds[loadingMessageIndex]),
        style = Typography.headlineSmall,
        textAlign = TextAlign.Center,
        modifier = modifier
            .alpha(loadingMessageAlpha.value)
    )
}

@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit = {},
    onClicked: (() -> Unit)? = null,
    placeholderText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = Typography.bodyMedium.copy(color = Linen),
    label: String,
    isError: Boolean = false,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = RoundedCornerShape(6.dp),
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Typography.labelLarge
        )
        TextSelectionHandler(enabled) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(shape)
                    .run {
                        if (onClicked != null) {
                            clickable(onClick = onClicked)
                        } else {
                            this
                        }
                    },
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholderText?.let {
                    {
                        Text(
                            text = it,
                            style = Typography.bodyMedium.copy(color = MidGrey)
                        )
                    }
                },
                enabled = enabled,
                readOnly = readOnly,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = if (enabled) Linen else Color.Transparent
                ),
                textStyle = textStyle,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                isError = isError,
                keyboardOptions = keyboardOptions.copy(
                    capitalization = capitalization
                ),
                keyboardActions = keyboardActions,
                shape = shape,
                trailingIcon = trailingIcon
            )
        }
    }
}

/***
 * If [textSelectionEnabled] is set to false then the content text composable will have the toolbox (copy paste etc)
 * and selection handles disabled. Otherwise if set to true the composable will be composed as normal.
 */
@Composable
private fun TextSelectionHandler(textSelectionEnabled: Boolean, content: @Composable () -> Unit) {
    if (!textSelectionEnabled) {
        val disabledTextSelectionColors = TextSelectionColors(
            handleColor = Color.Transparent,
            backgroundColor = Color.Transparent
        )
        CompositionLocalProvider(
            LocalTextToolbar provides EmptyTextToolbar,
            LocalTextSelectionColors provides disabledTextSelectionColors,
        ) {
            content()
        }
    } else {
        content()
    }
}

private object EmptyTextToolbar : TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {}

    override fun showMenu(
        rect: androidx.compose.ui.geometry.Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
    }
}