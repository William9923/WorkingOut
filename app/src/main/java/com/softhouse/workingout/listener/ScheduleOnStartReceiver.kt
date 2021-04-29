package com.softhouse.workingout.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.softhouse.workingout.shared.Constants
import dagger.android.DaggerBroadcastReceiver

class ScheduleOnStartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val timeInMillis = intent?.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)

        Log.d("Receiver", "On Start Service Receive")
    }
}