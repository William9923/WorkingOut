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
import io.karn.notify.Notify

class ScheduleOnStopReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        // Add id as extra intent -> find in db for the target
        val scheduleService = ScheduleService(context!!)
        when (intent?.action) {

            StepTrackerService.ACTION_STOP_SERVICE_STEP -> {
                Log.d("OnStopReceiver", "SingleSTEPAlarmTriggered")
                scheduleService.sendStepCommandToService(
                    context,
                    StepTrackerService.ACTION_STOP_SERVICE_STEP
                )
                buildNotification(context, "Workout Alarm", "Stop Running!!")
            }

            GeoTrackerService.ACTION_STOP_SERVICE_GEO -> {
                Log.d("OnStopReceiver", "SingleGEOAlarmTriggered")
                scheduleService.sendLocationCommandToService(
                    context,
                    GeoTrackerService.ACTION_STOP_SERVICE_GEO
                )
                buildNotification(context, "Workout Alarm", "Stop Cycling!!")
            }
        }
    }

    private fun buildNotification(context: Context, title: String, message: String) {
        Notify
            .with(context)
            .content {
                this.title = title
                text = message
                // TODO : change message into including start - end hour for training
            }
            .show()
    }
}