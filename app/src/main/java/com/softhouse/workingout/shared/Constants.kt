package com.softhouse.workingout.shared

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng

object Constants {

    const val APP_DATABASE_NAME = "workout_database"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val EXTRA_EXACT_ALARM_TIME = "EXTRA_EXACT_ALARM_TIME"

    const val ACTION_STEP_SINGLE_ALARM_TIME = "ACTION_STEP_SINGLE_ALARM_TIME"
    const val ACTION_GEO_SINGLE_ALARM_TIME = "ACTION_GEO_SINGLE_ALARM_TIME"
    const val ACTION_STEP_REPEATING_ALARM_TIME = "ACTION_STEP_REPEATING_ALARM_TIME"
    const val ACTION_GEO_REPEATING_ALARM_TIME = "ACTION_GEO_REPEATING_ALARM_TIME"
    const val ACTION_REPEATING_WEEK_ALARM_TIME = "ACTION_REPEATING_WEEK_ALARM_TIME"

    const val EXTRA_EXACT_END_ALARM_TIME = "EXTRA_END_ALARM_TIME"

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val TIMER_UPDATE_INTERVAL = 50L

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.GREEN
    const val POLYLINE_WIDTH = 8f

    const val NOTIFICATION_MAIN_ACTIVITY_CODE = 0

    const val NOTIFICATION_CHANNEL_ID = "workout_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Workout"
    const val NOTIFICATION_ID = 99

    const val ALARM_NOTIFICATION_CHANNEL_ID = "workout_reminder_channel"
    const val ALARM_NOTIFICATION_CHANNEL_NAME = "Workout Reminder"
    const val ALARM_NOTIFICATION_ID = 88

    const val INVALID_ID_DB = -1L
    const val INVALID_DATE = -1
    const val INVALID_MONTH = -1
    const val INVALID_YEAR = -1
}


typealias Polyline = MutableList<LatLng>