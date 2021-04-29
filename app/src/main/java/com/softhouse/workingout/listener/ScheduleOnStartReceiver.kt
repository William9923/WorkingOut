package com.softhouse.workingout.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.ScheduleService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.shared.Constants
import dagger.hilt.android.AndroidEntryPoint
import io.karn.notify.Notify
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleOnStartReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        // Add id as extra intent -> find in db for the target
        val scheduleService = ScheduleService(context!!)
        when (intent?.action) {

            StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP -> {
                Log.d("OnStartReceiver", "SingleSTEPAlarmTriggered")
                scheduleService.sendStepCommandToService(
                    context,
                    StepTrackerService.ACTION_START_OR_RESUME_SERVICE_STEP
                )
                buildNotification(context, "Workout Alarm", "Go Running!!")
            }

            GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO -> {
                Log.d("OnStartReceiver", "SingleGEOAlarmTriggered")
                scheduleService.sendLocationCommandToService(
                    context,
                    GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO
                )
                buildNotification(context, "Workout Alarm", "Go Cycling!!")
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