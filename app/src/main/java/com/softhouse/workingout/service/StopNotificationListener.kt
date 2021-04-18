package com.softhouse.workingout.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopNotificationListener : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action != null) {
            // For compass notification
//            if (intent.action.equals(CompassService.KEY_NOTIFICATION_STOP_ACTION)) {
//                context?.let {
//                    val notificationManager =
//                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                    val locateIntent = Intent(context, CompassService::class.java)
//                    context.stopService(locateIntent)
//                    val notificationId = intent.getIntExtra(CompassService.KEY_NOTIFICATION_ID, -1)
//                    if (notificationId != -1) {
//                        notificationManager.cancel(notificationId)
//                    }
//                }
//            }

            // For step counter notification
            if (intent.action.equals(StepCounterService.KEY_NOTIFICATION_STOP_ACTION)) {
                context?.let {
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val locateIntent = Intent(context, StepCounterService::class.java)
                    context.stopService(locateIntent)
                    val notificationId = intent.getIntExtra(StepCounterService.KEY_NOTIFICATION_ID, -1)
                    if (notificationId != -1) {
                        notificationManager.cancel(notificationId)
                    }
                }
            }

        }
    }
}