package com.padgett.progressnotes.injection.module

import com.padgett.progressnotes.injection.ActivityProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ActivityProviderEntryPoint {
    val activityProvider: ActivityProvider
}