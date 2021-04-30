package com.softhouse.workingout.shared

import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtility {

    fun getCalender(day: Int, month: Int, year: Int): Calendar {
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month - 1) // starts from 0
        calender.set(Calendar.DAY_OF_MONTH, day)
        calender.set(Calendar.HOUR_OF_DAY, 0)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        return calender
    }

    fun getCalenderFromMillis(millis: Long) : Calendar {
        val calender = Calendar.getInstance()
        calender.timeInMillis = millis
        return calender
    }

    fun getTimeMeasurementFromMillis(millis: Long) : String {
        var milliseconds = millis
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        val fractionHoursFromMinutes = (minutes / 60.0).roundTo(2)
        val fractionHoursFromSeconds = (seconds / 3600.0).roundTo(3)
        val fractionMinutesFromSeconds = (seconds / 60.0).roundTo(2)

        // Hours timestamp
        if (hours > 0)
            return "${hours + fractionHoursFromMinutes + fractionHoursFromSeconds} hour(s)"
        // Minutes timestamp
        if (minutes > 0)
        return "${minutes + fractionMinutesFromSeconds} minute(s)"

        // Seconds timestamp
        if (seconds > 0)
        return "$seconds second(s)"

        // Millisecond timestamp
        return "$millis ms"
    }
}