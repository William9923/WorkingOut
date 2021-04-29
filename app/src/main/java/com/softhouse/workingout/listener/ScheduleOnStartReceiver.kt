package com.softhouse.workingout.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.ScheduleService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.shared.Constants
import dagger.android.DaggerBroadcastReceiver

class ScheduleOnStartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        val scheduleService = ScheduleService(context!!)

        when (intent?.action) {
            Constants.ACTION_STEP_SINGLE_ALARM_TIME -> {
                Log.d("OnStartReceiver", "SingleSTEPAlarmTriggered")
                // TODO : Jalanin service
                scheduleService.sendStepCommandToService(context!!, StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP)
                // TODO : Create Notification for starting
                // TODO : Create alarm buat matiin
            }
            Constants.ACTION_STEP_REPEATING_ALARM_TIME -> {
                Log.d("OnStartReceiver", "RepeatingSTEPAlarmTriggered")
            }
            Constants.ACTION_GEO_SINGLE_ALARM_TIME -> {
                Log.d("OnStartReceiver", "SingleGEOAlarmTriggered")
            }
            Constants.ACTION_GEO_REPEATING_ALARM_TIME -> {
                Log.d("OnStartReceiver", "RepeatingGEOAlarmTriggered")
            }
        }
    }

    // TODO : Notif builder


}