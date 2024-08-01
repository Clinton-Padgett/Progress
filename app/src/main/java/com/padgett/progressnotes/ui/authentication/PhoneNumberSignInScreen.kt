package com.padgett.progressnotes.ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arpitkatiyarprojects.countrypicker.CountryPickerOutlinedTextField
import com.arpitkatiyarprojects.countrypicker.models.Dimensions
import com.arpitkatiyarprojects.countrypicker.utils.CountryPickerUtils
import com.padgett.progressnotes.R
import com.padgett.progressnotes.ui.authentication.PhoneNumberSignInUiViewState.DisplayMode
import com.padgett.progressnotes.ui.common.BackNavBar
import com.padgett.progressnotes.ui.common.PrimaryTextButton
import com.padgett.progressnotes.ui.common.stringResource
import com.padgett.progressnotes.ui.theme.Black
import com.padgett.progressnotes.ui.theme.MidGrey
import com.padgett.progressnotes.ui.theme.ProgressNotesTheme
import com.padgett.progressnotes.ui.theme.Typography

@Composable
fun PhoneNumberSignInScreen(
    viewModel: PhoneNumberSignInViewModel,
    onSignInSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    MainContent(
        uiState = uiState,
        onCountryCodeChanged = viewModel::onCountryCodeChanged,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onCodeChanged = viewModel::onCodeChanged,
        onContinueClicked = {
            keyboardController?.hide()
            viewModel.onContinueClicked(onSignInSuccess)
        },
        onBackClicked = { viewModel.onBackClicked() }
    )
}

@Composable
private fun MainContent(
    uiState: PhoneNumberSignInUiViewState,
    onCountryCodeChanged: (number: String) -> Unit,
    onPhoneNumberChanged: (number: String) -> Unit,
    onCodeChanged: (code: String) -> Unit,
    onContinueClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val isContinueButtonEnabled: Boolean =
        (uiState.displayMode == DisplayMode.CODE && uiState.code.length == 6) ||
                (uiState.displayMode == DisplayMode.NUMBER && uiState.timeOut == 0 &&
                        CountryPickerUtils.isMobileNumberValid(uiState.countryCode + uiState.phoneNumber))
    Surface(
        modifier = Modifier
            .background(Black)
            .systemBarsPadding()
            .fillMaxSize()
            .imePadding()
    ) {
        if (uiState.displayMode == DisplayMode.CODE) {
            BackNavBar(onBackClicked = onBackClicked)
        }
        Column(modifier = Modifier.padding(top = 48.dp)) {
            Text(
                text = stringResource(stringResource = uiState.title),
                style = Typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 48.dp, end = 48.dp, bottom = 24.dp)
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(stringResource = uiState.message),
                style = Typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
            )
            when (uiState.displayMode) {
                DisplayMode.CODE -> {
                    Spacer(modifier = Modifier.weight(1F))
                    CodeTextField(
                        value = uiState.code,
                        onValueChange = onCodeChanged,
                        isContinueButtonEnabled = isContinueButtonEnabled,
                        onContinueClicked = onContinueClicked
                    )
                }
                DisplayMode.NUMBER -> {
                    Spacer(modifier = Modifier.weight(0.8F))
                    PhoneNumberEntry(
                        phoneNumber = uiState.phoneNumber,
                        isContinueButtonEnabled = isContinueButtonEnabled,
                        onCountryCodeChanged = onCountryCodeChanged,
                        onPhoneNumberChanged = onPhoneNumberChanged,
                        onContinueClicked = onContinueClicked
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1F))
            if (uiState.displayMode == DisplayMode.CODE) {
                Text(
                    text = stringResource(R.string.phone_sign_in_screen_code_disclaimer),
                    style = Typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 48.dp, end = 48.dp)
                )
            }
            PrimaryTextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                isEnabled = isContinueButtonEnabled,
                text = if (uiState.displayMode == DisplayMode.NUMBER && uiState.timeOut > 0) {
                    uiState.timeOut.toString()
                } else {
                    stringResource(id = R.string.continue_button)
                },
                onClick = onContinueClicked
            )
        }
    }
}

@Composable
private fun PhoneNumberEntry(
    phoneNumber: String,
    isContinueButtonEnabled: Boolean,
    onCountryCodeChanged: (number: String) -> Unit,
    onPhoneNumberChanged: (number: String) -> Unit,
    onContinueClicked: () -> Unit
) {
    val view = LocalView.current
    Text(
        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
        text = stringResource(R.string.phone_sign_in_screen_mobile_label),
        style = Typography.labelLarge
    )
    if (view.isInEditMode) {
        Text(
            text = "Phone number. Preview unavailable.",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(MidGrey)
                .padding(16.dp)
                .fillMaxWidth()
        )
    } else {
        CountryPickerOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            mobileNumber = phoneNumber,
            onMobileNumberChange = onPhoneNumberChanged,
            onCountrySelected = { onCountryCodeChanged.invoke(it.countryPhoneNumberCode) },
            countryFlagDimensions = Dimensions(width = 40.dp, height = 25.dp),
            placeholder = {
                Text(text = stringResource(R.string.phone_sign_in_screen_mobile_hint), style = Typography.bodyMedium, color = MidGrey)
            },
            shape = RoundedCornerShape(6.dp),
            onDone = {
                if (isContinueButtonEnabled) onContinueClicked.invoke()
            }
        )
    }
}

@Composable
fun CodeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isContinueButtonEnabled: Boolean,
    onContinueClicked: () -> Unit
) {
    BasicTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (isContinueButtonEnabled) onContinueClicked.invoke()
        }),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
                repeat(PhoneNumberSignInViewModel.CODE_LENGTH) { index ->
                    Text(
                        modifier = Modifier
                            .border(
                                1.dp,
                                color = MidGrey,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .width(44.dp)
                            .padding(vertical = 20.dp),
                        text = value.getOrNull(index)?.toString() ?: "",
                        textAlign = TextAlign.Center,
                        style = Typography.headlineSmall
                    )
                }
            }
        })
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    ProgressNotesTheme {
        MainContent(
            uiState = PhoneNumberSignInUiViewState(displayMode = DisplayMode.CODE, code = "1234"),
            onCountryCodeChanged = {},
            onPhoneNumberChanged = {},
            onCodeChanged = {},
            onContinueClicked = {},
            onBackClicked = {}
        )
    }
}