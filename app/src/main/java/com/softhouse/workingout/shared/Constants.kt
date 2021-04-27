package com.softhouse.workingout.shared

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng

object Constants {

    const val APP_DATABASE_NAME = "workout_database"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_STOP_NOTIFICATION = "ACTION_STOP_NOTIFICATION"

    const val TIMER_UPDATE_INTERVAL = 50L

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.GREEN
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    const val NOTIFICATION_MAIN_ACTIVITY_CODE = 0
    const val NOTIFICATION_STOP_CODE = 99

    const val NOTIFICATION_CHANNEL_ID = "workout_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Workout"
    const val NOTIFICATION_ID = 99

    const val INVALID_ID_DB = -1L
    const val INVALID_DATE = -1
    const val INVALID_MONTH = -1
    const val INVALID_YEAR = -1
}


typealias Polyline = MutableList<LatLng>