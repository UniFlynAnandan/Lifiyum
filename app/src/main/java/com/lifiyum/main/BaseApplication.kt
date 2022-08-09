package com.lifiyum.main

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp


import timber.log.Timber

@HiltAndroidApp
class BaseApplication:Application() , Application.ActivityLifecycleCallbacks  {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        registerActivityLifecycleCallbacks(this);
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    override fun onActivityStarted(p0: Activity) {


        p0.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    override fun onActivityResumed(p0: Activity) {


    }

    override fun onActivityPaused(p0: Activity) {


    }

    override fun onActivityStopped(p0: Activity) {


    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {


        p0.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    override fun onActivityDestroyed(p0: Activity) {


    }
}