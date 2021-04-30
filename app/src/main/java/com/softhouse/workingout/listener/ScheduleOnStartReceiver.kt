package com.softhouse.workingout.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.softhouse.workingout.data.db.Schedule
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.ScheduleService
import com.softhouse.workingout.service.StartScheduleService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.sensor.tracker.Mode
import dagger.hilt.android.AndroidEntryPoint
import io.karn.notify.Notify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleOnStartReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    var schedule: Schedule? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val isAutoStart = intent?.getBooleanExtra(Constants.START_SERVICE_FLAG, true)
        val isRepetitive = intent?.getBooleanExtra(Constants.REPETITIVE_FLAG, false)
        val interval = intent?.getLongExtra(Constants.INTERVAL_FLAG, 0L)
        val id = intent?.getLongExtra(Constants.SCHEDULE_ID_FLAG, Constants.INVALID_ID_DB) ?: Constants.INVALID_ID_DB

        schedule = mainRepository.getSpecificScheduleById(id).value

        if (schedule == null)
            return

        Log.d("Receiver", "Before Receive context")
        val scheduleService = ScheduleService(context!!)
        Log.d("Receiver", "After Receive context")
        val mode = schedule!!.mode

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
                val text =
                    if (schedule!!.target > 0L) "Go Run!! Current Target: ${schedule?.target} steps" else "Currently no target for running"
                buildNotification(context, "Workout Alarm", text)
            }

            GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO -> {
                Log.d("OnStartReceiver", "SingleGEOAlarmTriggered")

                if (isAutoStart!!) {
                    scheduleService.sendLocationCommandToService(
                        context,
                        GeoTrackerService.ACTION_START_OR_RESUME_SERVICE_GEO
                    )
                }
                val text =
                    if (schedule!!.target > 0L) "Go Cycling!! Current Target: ${schedule?.target} km" else "Currently no target for cycling"
                buildNotification(context, "Workout Alarm", text)
            }
        }

        if (isRepetitive == true) {
            Log.d("Scheduler", "Create repetitive")
            setRepetitiveAlarm(
                StartScheduleService(context),
                interval ?: 0L,
                mode,
                isAutoStart ?: false,
                id
            )
        } else {
            // single
            deleteSchedule(schedule!!)
        }
    }

    private fun setRepetitiveAlarm(
        service: StartScheduleService,
        interval: Long,
        mode: Mode,
        autoStart: Boolean,
        id: Long
    ) {
//        val cal = Calendar.getInstance()
//        Log.d("OnStopReceiver", "Current : ${cal.timeInMillis}")
//        Log.d("OnStopReceiver", "Interval : $interval")
//        cal.timeInMillis = cal.timeInMillis + interval
//        Log.d("OnStopReceiver", "After : ${cal.timeInMillis}")
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + interval
        }
        service.setRepeatingAlarm(cal.timeInMillis, mode, id, autoStart, interval)
    }

    private fun buildNotification(context: Context, title: String, message: String) {
        Notify
            .with(context)
            .content {
                this.title = title
                text = message
            }
            .show()
    }

    private fun deleteSchedule(schedule: Schedule) {
        val appScope = CoroutineScope(SupervisorJob())
        // Coroutine : IO Dispatchers because saving to db can wait ...
        appScope.launch(Dispatchers.IO) {
            with(mainRepository) {
                val id = deleteSchedule(schedule)
                Log.d("Database", "Deleted Schedule ID : $id")
            }
        }
    }
}