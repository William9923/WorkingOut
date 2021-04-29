package com.softhouse.workingout.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.softhouse.workingout.listener.ScheduleOnStartReceiver
import com.softhouse.workingout.listener.ScheduleOnStopReceiver
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.sensor.tracker.Mode
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

class ScheduleService(private val context: Context) {

    private val alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setSingleAlarm(date: Calendar, startTime: Calendar, endTime: Calendar, mode: Mode) {
        Log.d("Alarm", "Setting Up alarm")
        val alarm = Calendar.getInstance()
        alarm.add(Calendar.SECOND, 5)
//        alarm.set(
//            date.get(Calendar.YEAR),
//            date.get(Calendar.MONTH),
//            date.get(Calendar.DAY_OF_MONTH),
//            startTime.get(Calendar.HOUR_OF_DAY),
//            startTime.get(Calendar.MINUTE),
//            startTime.get(Calendar.SECOND)
//        )
//        Log.d("Alarm", "Will occur in : ${alarm.get(Calendar.DAY_OF_MONTH)} " +
//                "| ${alarm.get(Calendar.MONTH)}" +
//                "| ${alarm.get(Calendar.HOUR_OF_DAY)} : ${alarm.get(Calendar.MINUTE)} : ${alarm.get(Calendar.SECOND)}")
        val intentAction = when (mode) {
            Mode.STEPS -> Constants.ACTION_STEP_SINGLE_ALARM_TIME
            Mode.CYCLING -> Constants.ACTION_GEO_SINGLE_ALARM_TIME
        }
        Log.d("Alarm", "Millis : ${alarm.timeInMillis}")
        Log.d("Alarm", "Current Time : ${System.currentTimeMillis()}")

        setAlarm(
            alarm.timeInMillis,
            getPendingIntent(
                getStartIntent().apply {
                    action = intentAction
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, alarm.timeInMillis)
                    putExtra(Constants.EXTRA_EXACT_END_ALARM_TIME, endTime.timeInMillis)
                }
            )
        )
    }

    fun setRepeatingAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getStartIntent().apply {
                    action = Constants.ACTION_STEP_REPEATING_ALARM_TIME
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMillis)
                }
            )
        )
    }

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            Constants.ALARM_NOTIFICATION_ID,
            intent,
            FLAG_UPDATE_CURRENT
        )

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        if (alarmManager == null) {
            Log.d("Alarm", "Alarm Manager Not available")
        }
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun getStartIntent() = Intent(context, ScheduleOnStartReceiver::class.java)

    private fun getStopIntent() = Intent(context, ScheduleOnStopReceiver::class.java)

    // Service related Intent
    fun sendStepCommandToService(context: Context, action: String) =
        Intent(context, StepTrackerService::class.java).also {
            it.action = action
            context.startService(it)
        }

    fun sendLocationCommandToService(context: Context, action: String) =
        Intent(context, GeoTrackerService::class.java).also {
            it.action = action
            context.startService(it)
        }

}