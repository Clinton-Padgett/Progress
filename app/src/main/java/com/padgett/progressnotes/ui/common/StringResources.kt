package com.padgett.progressnotes.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.padgett.progressnotes.domain.common.models.StringResource

@Composable
@ReadOnlyComposable
fun stringResource(stringResource: StringResource): String {
    val resources = LocalContext.current.resources
    return if (stringResource.args.isEmpty()) {
        resources.getString(stringResource.resId)
    } else {
        resources.getString(stringResource.resId, *stringResource.args.toTypedArray())
    }
}