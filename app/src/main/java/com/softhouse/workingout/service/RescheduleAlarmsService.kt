package com.softhouse.workingout.service

import android.app.Service
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.observe
import com.softhouse.androidutils.Result
import com.softhouse.workingout.data.repository.AlarmRepository
import com.softhouse.workingout.alarm.schedule
import org.koin.android.ext.android.inject

class RescheduleAlarmsService : LifecycleService() {
    private val repository: AlarmRepository by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        repository.observeAlarms().observe(this) {
            val alarms = (it as? Result.Success)?.data ?: return@observe
            alarms.forEach { alarm ->
                alarm.schedule(applicationContext)
            }
        }
        return Service.START_STICKY
    }
}
