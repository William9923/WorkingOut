package com.softhouse.workingout.listener

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.softhouse.workingout.data.db.AppDatabase
import com.softhouse.workingout.data.db.Schedule
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.ScheduleService
import com.softhouse.workingout.service.StepTrackerService
import com.softhouse.workingout.service.StopScheduleService
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.MainActivity
import com.softhouse.workingout.ui.sensor.tracker.Mode
import dagger.android.DaggerBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import io.karn.notify.Notify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ScheduleOnStopReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    var schedule: Schedule? = null

    override fun onReceive(context: Context, intent: Intent) {

        val isAutoStart = intent.getBooleanExtra(Constants.START_SERVICE_FLAG, true)
        val isRepetitive = intent.getBooleanExtra(Constants.REPETITIVE_FLAG, false)
        val interval = intent.getLongExtra(Constants.INTERVAL_FLAG, 0L)
        val id = intent.getLongExtra(Constants.SCHEDULE_ID_FLAG, Constants.INVALID_ID_DB)

//        schedule = mainRepository.getSpecificScheduleById(id).value
//        Log.d("Database", mainRepository.getSpecificScheduleById(id).value.toString())
//        Log.d("Database", mainRepository.getAllSchedule().value.toString())
//
//        mainRepository.getAllSchedule().observeForever { it ->
//            it.forEach { data ->
//                Log.d("Database", data.id.toString())
//            }
//        }

        mainRepository.getSpecificScheduleById(id).observeForever {
            schedule = it

            if (schedule == null) {
                Log.d("Receiver", "ID : $id")
                Log.d("Receiver", "Schedule is null")

            } else {


                val scheduleService = ScheduleService(context)
                val mode = schedule!!.mode


                when (intent.action) {

                    StepTrackerService.ACTION_STOP_SERVICE_STEP -> {
                        Log.d("OnStopReceiver", "SingleSTEPAlarmTriggered")


                        if (isAutoStart) {
                            scheduleService.sendStepCommandToService(
                                context,
                                StepTrackerService.ACTION_STOP_SERVICE_STEP
                            )
                        }

                        buildNotification(
                            context,
                            "Workout Alarm",
                            "Stop!! You done a good job for this running workout\n Click here to see result"
                        )
                    }

                    GeoTrackerService.ACTION_STOP_SERVICE_GEO -> {
                        Log.d("OnStopReceiver", "SingleGEOAlarmTriggered")

                        if (isAutoStart) {
                            scheduleService.sendLocationCommandToService(
                                context,
                                GeoTrackerService.ACTION_STOP_SERVICE_GEO
                            )
                        }

                        buildNotification(
                            context,
                            "Workout Alarm",
                            "Stop!! You done a good job for this cycling workout\n Click me to see result"
                        )
                    }
                }
                if (isRepetitive) {
                    Log.d("Scheduler", "Create repetitive")
                    setRepetitiveAlarm(
                        StopScheduleService(context),
                        interval,
                        mode,
                        isAutoStart,
                        id
                    )
                } else {
                    // single
                    deleteSchedule(schedule!!)
                }
            }
        }
        Log.d("Receiver", "End")
    }

    private fun setRepetitiveAlarm(
        service: StopScheduleService,
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
            .meta {
                clickIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java).apply {
                        action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
                    },
                    0
                )
            }
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