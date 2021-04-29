package com.softhouse.workingout.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.softhouse.workingout.service.*
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.sensor.tracker.Mode
import dagger.android.DaggerBroadcastReceiver
import io.karn.notify.Notify
import java.util.*

class ScheduleOnStopReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        val isRepetitive = intent?.getBooleanExtra(Constants.REPETITIVE_FLAG, false)
        val interval = intent?.getLongExtra(Constants.INTERVAL_FLAG, 0L)
        // Add id as extra intent -> find in db for the target
        val scheduleService = ScheduleService(context!!)
        var mode = Mode.STEPS

        when (intent?.action) {

            StepTrackerService.ACTION_STOP_SERVICE_STEP -> {
                Log.d("OnStopReceiver", "SingleSTEPAlarmTriggered")
                scheduleService.sendStepCommandToService(
                    context,
                    StepTrackerService.ACTION_STOP_SERVICE_STEP
                )
                buildNotification(context, "Workout Alarm", "Stop Running!!")
                mode = Mode.STEPS
            }

            GeoTrackerService.ACTION_STOP_SERVICE_GEO -> {
                Log.d("OnStopReceiver", "SingleGEOAlarmTriggered")
                scheduleService.sendLocationCommandToService(
                    context,
                    GeoTrackerService.ACTION_STOP_SERVICE_GEO
                )
                buildNotification(context, "Workout Alarm", "Stop Cycling!!")
                mode = Mode.CYCLING
            }
        }
        if (isRepetitive == true) {
            Log.d("Scheduler", "Create repetitive")
            setRepetitiveAlarm(StopScheduleService(context), interval ?: 0L, mode)
        }
    }

    private fun setRepetitiveAlarm(service: StopScheduleService, interval: Long, mode: Mode) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + interval
        }
        service.setRepeatingAlarm(cal.timeInMillis, mode)
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