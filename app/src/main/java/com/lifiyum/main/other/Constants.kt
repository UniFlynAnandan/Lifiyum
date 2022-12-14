package com.lifiyum.main.other

import android.graphics.Color

object Constants {

    const val RUNNING_DATABASE_NAME = "lifiyum_db"
    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val REQUEST_CODE_ACTION_FINISH_RUN = 3

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_FINISH_RUN = "ACTION_FINISH_RUN"

    const val TIMER_UPDATE_INTERVAL = 50L

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val NOTIFICATION_CHANNEL_TARGET_ID = "target_reached_channel"
    const val NOTIFICATION_CHANNEL_TARGET_NAME = "Target reached"
    const val NOTIFICATION_TARGET_ID = 2
    ///shared_preferences

    const val SHARED_PREFERENCES_NAME = "com.lifiyum.main"
    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"
    const val KEY_TARGET_TYPE = "KEY_TARGET_TYPE"
    const val KEY_TARGET_VALUE = "KEY_TARGET_VALUE"
    const val KEY_ACTIVITY_TYPE = "KEY_ACTIVITY_TYPE"
    const val KEY_AUTO_PAUSE = "KEY_AUTO_PAUSE"


    const val CANCEL_TRACKING_DIALOG_TAG = "CANCEL_TRACKING_DIALOG_TAG"
}
