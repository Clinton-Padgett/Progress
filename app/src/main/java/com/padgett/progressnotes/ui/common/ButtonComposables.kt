package com.padgett.progressnotes.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.padgett.progressnotes.R
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography

@Composable
fun BackNavBar(modifier: Modifier = Modifier, onBackClicked: () -> Unit) {
    Column(modifier = modifier.fillMaxWidth()) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier
                .padding(start = 12.dp, top = 4.dp)
                .systemBarsPadding()
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_chevron_left), contentDescription = "")
        }
    }
}

@Composable
fun PrimaryTextButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) =
    Button(
        modifier = modifier.wrapContentHeight(),
        enabled = isEnabled,
        onClick = onClick,
        shape = RoundedCornerShape(6.dp)
    ) {
        icon?.let {
            icon()
        }
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            style = Typography.titleLarge
        )
    }

@Composable
fun SecondaryTextButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean = true,
    style: TextStyle = Typography.titleLarge,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    selected: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) =
    OutlinedButton(
        modifier = modifier.wrapContentHeight(),
        onClick = onClick,
        enabled = isEnabled,
        contentPadding = contentPadding,
        shape = RoundedCornerShape(6.dp)
    ) {
        icon?.let {
            icon()
        }
        Text(
            text = text,
            style = style
        )
    }

@Preview
@Composable
fun PreviewButtons() {
    ProgressNotesTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(verticalArrangement = Arrangement.Center) {
                BackNavBar { }
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryTextButton(modifier = Modifier.fillMaxWidth(), text = "Primary button full width") { }
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryTextButton(text = "Primary button wrap width") { }
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryTextButton(modifier = Modifier.fillMaxWidth(), text = "Secondary button full width") { }
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryTextButton(text = "Secondary button wrap width") { }
            }
        }
    }
}