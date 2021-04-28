package com.softhouse.workingout.alarm

import androidx.core.view.get
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.size
import com.softhouse.workingout.R
import com.softhouse.workingout.data.Alarm
import com.softhouse.workingout.databinding.ActivityActiveAlarmBinding
import com.softhouse.workingout.databinding.AlarmViewBinding
import com.softhouse.workingout.databinding.FragmentAlarmEditBinding
import com.softhouse.workingout.databinding.FragmentAlarmBinding
//import com.softhouse.workingout.music.api.AlbumImageSize
//import com.softhouse.workingout.music.api.NapsterService
import java.time.format.DateTimeFormatter

fun AlarmViewBinding.bind(alarm: Alarm) {
    val context = root.context
    val time = alarm.alarmTime
    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    alarmTimeTextView.text = time.format(formatter)
    if (alarm.hour >= Utils.NOON) {
        alarmTimePeriodTextView.text = context.getString(R.string.pm)
    } else {
        alarmTimePeriodTextView.text = context.getString(R.string.am)
    }
    if (alarm.albumId == "") {
        imageView.isVisible = true
        trackImageView.isVisible = false
    } else {
        imageView.isInvisible = true
        trackImageView.isVisible = true
        trackImageView.setImageUrl(
//            NapsterService.createAlbumImageUrl(
//                alarm.albumId,
//                AlbumImageSize.SIZE_200X200
//            )
            ""
        )
    }
    val days = alarm.days
    mondayTextView.isEnabled = (days and Alarm.MONDAY) > 0
    tuesdayTextView.isEnabled = (days and Alarm.TUESDAY) > 0
    wednesdayTextView.isEnabled = (days and Alarm.WEDNESDAY) > 0
    thursdayTextView.isEnabled = (days and Alarm.THURSDAY) > 0
    fridayTextView.isEnabled = (days and Alarm.FRIDAY) > 0
    saturdayTextView.isEnabled = (days and Alarm.SATURDAY) > 0
    sundayTextView.isEnabled = (days and Alarm.SUNDAY) > 0
    alarmSwitch.isChecked = alarm.enabled
}

fun FragmentAlarmEditBinding.setup(exists: Boolean, alarm: Alarm) {
    val context = root.context
    alarm.enabled = true
    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    alarmTimeTextView.text = alarm.alarmTime.format(formatter)
    if (alarm.hour >= Utils.NOON) {
        alarmTimePeriodTextView.text = context.getString(R.string.pm)
    } else {
        alarmTimePeriodTextView.text = context.getString(R.string.am)
    }
    val days = alarm.days
    for (i in 0 until daysToggleGroup.size) {
        val view = daysToggleGroup[i]
        if (days and (1 shl i) > 0) {
            daysToggleGroup.check(view.id)
        }
    }
    vibrationFab.isChecked = alarm.vibrate
    snoozeFab.isChecked = alarm.snooze
    descriptionEditText.setText(alarm.description)
    deleteFab.isVisible = exists
}

fun FragmentAlarmBinding.setNearestAlarm(
    nearestAlarm: Alarm?
) {
    val context = root.context
    val nearestDateTime = nearestAlarm?.nearestDateTime()
    if (nearestAlarm == null || nearestDateTime == null) {
        allAlarmsOffTextView.isVisible = true
        nearestAlarmGroup.isInvisible = true
    } else {
        allAlarmsOffTextView.isInvisible = true
        nearestAlarmGroup.isVisible = true
        val durationText = calculateDurationString(context, nearestDateTime)
        durationTextView.text = durationText
        val formatter = DateTimeFormatter.ofPattern("hh:mm")
        nearestAlarmTextView.text = nearestDateTime.format(formatter)
        if (nearestAlarm.hour >= Utils.NOON) {
            nearestAlarmPeriodTextView.text = context.getString(R.string.pm)
        } else {
            nearestAlarmPeriodTextView.text = context.getString(R.string.am)
        }
    }
}

fun ActivityActiveAlarmBinding.bind(alarm: Alarm) {
    val context = root.context
    val time = alarm.alarmTime
    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    alarmTimeTextView.text = time.format(formatter)
    if (alarm.hour >= Utils.NOON) {
        alarmTimePeriodTextView.text = context.getString(R.string.pm)
    } else {
        alarmTimePeriodTextView.text = context.getString(R.string.am)
    }

    snoozeFab.isInvisible = !alarm.snooze
    descriptionTextView.text = alarm.description
    descriptionTextView.isInvisible = alarm.description.isEmpty()
}
