package com.padgett.progressnotes

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.padgett.progressnotes.injection.ActivityProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ProgressApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Give the ActivityProvider access to the activity
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                ActivityProvider.onActivityCreated(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                ActivityProvider.onActivityDestroyed(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }
}