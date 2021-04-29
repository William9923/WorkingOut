package com.softhouse.workingout.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.ScheduleService
import com.softhouse.workingout.service.StartScheduleService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.sensor.tracker.Mode
import dagger.hilt.android.AndroidEntryPoint
import io.karn.notify.Notify
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleOnStartReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        val isAutoStart = intent?.getBooleanExtra(Constants.START_SERVICE_FLAG, true)
        val isRepetitive = intent?.getBooleanExtra(Constants.REPETITIVE_FLAG, false)
        val interval = intent?.getLongExtra(Constants.INTERVAL_FLAG, 0L)

        Log.d("OnStartReceiver", "Interval : $interval")
        // Add id as extra intent -> find in db for the target
        val scheduleService = ScheduleService(context!!)
        var mode = Mode.STEPS

        when (intent?.action) {

            StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP -> {
                Log.d("OnStartReceiver", "SingleSTEPAlarmTriggered")

                if (isAutoStart!!) {
                    Log.d("Receiver", "OnStartReceiver called!")
                    scheduleService.sendStepCommandToService(
                        context,
                        StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP
                    )
                }
                buildNotification(context, "Workout Alarm", "Go Running!!")
                mode = Mode.STEPS
            }

            GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO -> {
                Log.d("OnStartReceiver", "SingleGEOAlarmTriggered")

                if (isAutoStart!!) {
                    scheduleService.sendLocationCommandToService(
                        context,
                        GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO
                    )
                }

                buildNotification(context, "Workout Alarm", "Go Cycling!!")
                mode = Mode.CYCLING
            }
        }

        if (isRepetitive == true) {
            Log.d("Scheduler", "Create repetitive")
            setRepetitiveAlarm(StartScheduleService(context), interval ?: 0L, mode, isAutoStart ?: false)
        }
    }

    private fun setRepetitiveAlarm(service: StartScheduleService, interval: Long, mode: Mode, autoStart: Boolean) {
        val cal = Calendar.getInstance()
        Log.d("OnStopReceiver", "Current : ${cal.timeInMillis}")
        Log.d("OnStopReceiver", "Interval : $interval")
        cal.timeInMillis = cal.timeInMillis + interval
        Log.d("OnStopReceiver", "After : ${cal.timeInMillis}")
        service.setRepeatingAlarm(cal.timeInMillis, mode, autoStart, interval)
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