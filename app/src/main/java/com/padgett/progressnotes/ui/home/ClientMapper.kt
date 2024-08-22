package com.padgett.progressnotes.ui.home

import com.padgett.progressnotes.domain.clients.models.TimeType

fun TimeType.mapToDisplay(): String =
    when (this) {
        TimeType.TRAVEL -> "Travel"
        TimeType.HOME_VISIT -> "Home Visit"
        TimeType.OFFICE -> "Office"
        TimeType.COMMUNITY_VISIT -> "Community Visit"
        TimeType.EQUIPMENT_PICK_UP -> "Equipment Pick Up"
    }