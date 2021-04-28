package com.softhouse.workingout.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.softhouse.workingout.service.RescheduleAlarmsService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            startRescheduleAlarmsService(context)
        }
    }

    private fun startRescheduleAlarmsService(context: Context) {
        val intent = Intent(context.applicationContext, RescheduleAlarmsService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
