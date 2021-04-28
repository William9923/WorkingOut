package com.softhouse.workingout.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.softhouse.workingout.R

const val CHANNEL_ID = "Working Out"
const val NOTIFICATION_ID = 42
const val CHANNEL_NAME = "WorkingOut"
const val CHANNEL_DESC = "WorkingOut"

class NotificationUtil(context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.GREEN
                shouldShowLights()
                setSound(null, null)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(
        context: Context,
        title: String,
        message: String,
        pendingIntent: PendingIntent? = null,
        snoozeIntent: PendingIntent? = null,
        turnOffIntent: PendingIntent? = null
    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_round_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, true)

        snoozeIntent?.let {
            builder.addAction(R.drawable.ic_snooze, context.getString(R.string.snooze), it)
        }
        turnOffIntent?.let {
            builder.addAction(R.drawable.ic_alarm_off, context.getString(R.string.turn_off), it)
        }

        return builder.build().apply {
            flags = flags or Notification.FLAG_INSISTENT
        }
    }
}
