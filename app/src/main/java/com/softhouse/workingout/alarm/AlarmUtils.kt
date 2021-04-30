package com.softhouse.workingout.alarm

import android.app.Activity
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.softhouse.workingout.R
import com.softhouse.workingout.alarm.active.ActiveAlarmActivity
import com.softhouse.workingout.listener.AlarmReceiver
import com.softhouse.workingout.data.Alarm
import com.softhouse.workingout.preferences.Preferences
import org.koin.core.component.KoinApiExtension
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

object Utils {
    const val HEIGHT_RATIO = 0.9f
    const val DISABLED_ALPHA = 0.4f
    const val ENABLED_ALPHA = 1f
    const val NOON = 12
    const val PROGRESS_FULL = 100f
}

fun calculateDurationString(
    context: Context,
    alarm: LocalDateTime,
    now: LocalDateTime = LocalDateTime.now()
): String {
    val resources = context.resources
    var duration = Duration.between(now.withSecond(0), alarm)
    if (duration.nano > 0) {
        duration = duration.plusSeconds(1)
    }
    val days = duration.toDays().toInt()
    duration = duration.minusDays(days.toLong())
    val hours = duration.toHours().toInt()
    duration = duration.minusHours(hours.toLong())
    val minutes = duration.toMinutes().toInt()
    val dayStr = resources.getQuantityString(R.plurals.number_of_days, days, days)
    val hourStr = resources.getQuantityString(R.plurals.number_of_hours, hours, hours)
    val minuteStr = resources.getQuantityString(R.plurals.number_of_minutes, minutes, minutes)
    return when {
        days > 0 ->
            context.getString(R.string.in_days_hours, dayStr, hourStr)
        hours > 0 ->
            context.getString(R.string.in_hours_minutes, hourStr, minuteStr)
        minutes > 0 ->
            context.getString(R.string.in_minutes, minuteStr)
        else -> context.getString(R.string.now)
    }
}

@KoinApiExtension
fun Context.createPendingIntentToActivity(alarm: Alarm): PendingIntent {
    val intent = Intent(this, ActiveAlarmActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra(AlarmReceiver.EXTRA_ALARM, alarm.toBundle())
    }
    return PendingIntent.getActivity(
        this,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

@KoinApiExtension
fun Context.createPendingIntentToBroadcast(
    alarm: Alarm,
    snoozed: Boolean = false
): PendingIntent {
    val intent = Intent(this, AlarmReceiver::class.java)
    intent.action = AlarmReceiver.ACTION_ALARM_FIRED
    intent.putExtra(AlarmReceiver.EXTRA_ALARM, alarm.toBundle())
    intent.putExtra(AlarmReceiver.EXTRA_ALARM_SNOOZE, snoozed)
    return PendingIntent.getBroadcast(
        this,
        alarm.alarmId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

@KoinApiExtension
fun Alarm.schedule(context: Context, snoozed: Boolean = false): LocalDateTime? {
    val date = nearestDateTime() ?: return null
    val time = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val sender = context.createPendingIntentToBroadcast(this, snoozed)
    val showOperation = context.createPendingIntentToActivity(this)

    val alarmClockInfo = AlarmManager.AlarmClockInfo(time, showOperation)
    alarmManager.setAlarmClock(alarmClockInfo, sender)
    return date
}

@KoinApiExtension
fun Alarm.cancel(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = context.createPendingIntentToBroadcast(this)
    alarmManager.cancel(pendingIntent)
}

@KoinApiExtension
fun Alarm.snooze(context: Context) {
    var future = alarmTime
    future = future.plusMinutes(Preferences.snoozeDuration.toLong())
    val snoozed = Alarm(
        future.hour,
        future.minute,
        true,
        0,
        vibrate,
        snooze,
        description,
        cycling,
        target,
        autotrack,
        endHour,
        endMinute,
        trackUrl,
        alarmId
    )
    val date = snoozed.schedule(context, true)
    date?.let {
        val text =
            context.getString(R.string.alarm_go_off) + " " + calculateDurationString(context, it)
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        var flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        flags = flags or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        window.addFlags(flags)
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(
                this@turnScreenOnAndKeyguardOff,
                object : KeyguardManager.KeyguardDismissCallback() {
                    override fun onDismissCancelled() {
                        super.onDismissCancelled()
                    }

                    override fun onDismissError() {
                        super.onDismissError()
                    }

                    override fun onDismissSucceeded() {
                        super.onDismissSucceeded()
                    }
                }
            )
        }
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
    } else {
        var flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        flags = flags or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        window.clearFlags(flags)
    }
}

fun TextToSpeech.setLanguageOrDefault(locale: Locale): Int {
    var res = setLanguage(locale)
    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
        res = setLanguage(Locale.US)
    }
    return res
}

fun ImageView.setImageUrl(url: String) {
    Glide
        .with(context)
        .load(url)
        .centerCrop()
        .placeholder(R.drawable.alarm_background)
        .into(this)
}

fun Context.isConnectedToInternet(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networks: Array<Network> = cm.allNetworks
    var hasInternet = false
    if (networks.isNotEmpty()) {
        for (network in networks) {
            val nc = cm.getNetworkCapabilities(network)
            if (nc!!.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) hasInternet = true
        }
    }
    return hasInternet
}
