package com.softhouse.workingout.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.softhouse.workingout.listener.ScheduleOnStartReceiver
import com.softhouse.workingout.shared.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

class ScheduleService(private val context: Context) {

    private val alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setSingleAlarm(date: Calendar, startTime: Calendar, endTime: Calendar) {
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

        Log.d("Alarm", "Millis : ${alarm.timeInMillis}")
        Log.d("Alarm", "Current Time : ${System.currentTimeMillis()}")

        setAlarm(
            alarm.timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = Constants.ACTION_SINGLE_ALARM_TIME
                    putExtra(Constants.EXTRA_EXACT_ALARM_TIME, alarm.timeInMillis)
                }
            )
        )
    }

    fun setRepeatingAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = Constants.ACTION_REPEATING_ALARM_TIME
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

    private fun getIntent() = Intent(context, ScheduleOnStartReceiver::class.java)

}