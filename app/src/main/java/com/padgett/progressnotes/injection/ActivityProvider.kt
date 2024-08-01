package com.padgett.progressnotes.injection

import android.app.Activity
import com.padgett.progressnotes.injection.module.ActivityProviderEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.internal.GeneratedComponentManagerHolder
import javax.inject.Inject

@ActivityRetainedScoped
class ActivityProvider @Inject constructor() {

    companion object {
        private fun Activity.withProvider(block: ActivityProvider.() -> Unit) {
            if (this is GeneratedComponentManagerHolder) {
                EntryPointAccessors.fromActivity<ActivityProviderEntryPoint>(this)
                    .activityProvider
                    .block()
            }
        }

        fun onActivityCreated(activity: Activity) {
            activity.withProvider {
                currentActivity = activity
            }
        }

        fun onActivityDestroyed(activity: Activity) {
            activity.withProvider {
                if (currentActivity === activity) {
                    currentActivity = null
                }
            }
        }
    }

    var currentActivity: Activity? = null
        private set
}