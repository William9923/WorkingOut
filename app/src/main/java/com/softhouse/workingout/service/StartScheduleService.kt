package com.softhouse.workingout.service

import android.content.Context
import android.content.Intent
import android.util.Log
import com.softhouse.workingout.listener.ScheduleOnStartReceiver
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.sensor.tracker.Mode
import java.util.*

class StartScheduleService(private val context: Context) : ScheduleService(context) {

    private fun getIntent() = Intent(context, ScheduleOnStartReceiver::class.java)

    // TODO : Debugging function can be stopped

    fun setSingleAlarm(timeInMillis: Long, mode: Mode) {
        Log.d("Alarm", "Setting Up start alarm")
        val alarm = Calendar.getInstance()
//        alarm.add(Calendar.SECOND, 5)
        alarm.timeInMillis = timeInMillis
        val intentAction = when (mode) {
            Mode.STEPS -> StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP
            Mode.CYCLING -> GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO
        }

        val requestCode = when (mode) {
            Mode.STEPS -> Constants.ALARM_STEP_SINGLE_NOTIFICATION_ID
            Mode.CYCLING -> Constants.ALARM_GEO_SINGLE_NOTIFICATION_ID
        }

        val pendingIntent = getPendingIntent(
            getIntent().apply {
                action = intentAction
                putExtra(Constants.REPETITIVE_FLAG, false)
            },
            requestCode
        )
        super.setAlarm(alarm.timeInMillis, pendingIntent)
    }

    fun setRepeatingAlarm(timeInMillis: Long, mode: Mode, interval: Long = 24 * 60 * 60 * 1000L) {
        Log.d("Alarm", "Setting Up start repeating alarm")
        val alarm = Calendar.getInstance()
//        alarm.add(Calendar.SECOND, 5)
        alarm.timeInMillis = timeInMillis
        val intentAction = when (mode) {
            Mode.STEPS -> StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP
            Mode.CYCLING -> GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO
        }

        val requestCode = when (mode) {
            Mode.STEPS -> Constants.ALARM_STEP_REPEATING_NOTIFICATION_ID
            Mode.CYCLING -> Constants.ALARM_GEO_REPEATING_NOTIFICATION_ID
        }

        val pendingIntent = getPendingIntent(
            getIntent().apply {
                action = intentAction
                putExtra(Constants.REPETITIVE_FLAG, true)
                putExtra(Constants.INTERVAL_FLAG, interval)
            },
            requestCode
        )
        super.setAlarm(alarm.timeInMillis, pendingIntent)
    }

    fun setRepeatingWeeksAlarm(listOfTime: List<Long>, mode: Mode) {
        Log.d("Alarm", "Setting Up week repeating alarm")
        listOfTime.forEach {
            setRepeatingAlarm(it, mode, 7 * 24 * 60 * 60 * 1000L)
        }
    }
}