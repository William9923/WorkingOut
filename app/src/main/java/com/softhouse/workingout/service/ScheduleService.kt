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

open class ScheduleService(private val context: Context) {

    private val alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    protected fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
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

    fun getPendingIntent(intent: Intent, requestCode: Int) =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            FLAG_UPDATE_CURRENT
        )

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