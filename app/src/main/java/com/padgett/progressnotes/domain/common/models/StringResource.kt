package com.padgett.progressnotes.domain.common.models

import androidx.annotation.StringRes

data class StringResource(
    @StringRes val resId: Int,
    val args: List<Any> = listOf()
)
