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

    fun setSingleAlarm(timeInMillis: Long, mode: Mode) {
        Log.d("Alarm", "Setting Up alarm")
        val alarm = Calendar.getInstance()
        alarm.add(Calendar.SECOND, 5)
//        alarm.timeInMillis = timeInMillis
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
            },
            requestCode
        )
        super.setSingleAlarm(alarm.timeInMillis, pendingIntent)
    }
}