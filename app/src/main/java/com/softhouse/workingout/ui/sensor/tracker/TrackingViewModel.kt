package com.softhouse.workingout.ui.sensor.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.softhouse.workingout.service.StepDetectorService

class TrackingViewModel : ViewModel() {
    /**
     * Application Logic State
     */
    private val _started = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _steps = MutableLiveData<Int>().apply {
        value = 0
    }

    // Location based variable

    private val _mode = MutableLiveData<Mode>().apply {
        value = Mode.STEPS
    }

    /**
     * Binding public variable
     */
    var started: LiveData<Boolean> = _started
    var steps: LiveData<Int> = _steps
    var mode: LiveData<Mode> = _mode


    /**
     * Tracking Application Logic
     */
    fun start() {
        Log.d("TrackingViewModel", "Start!")
        _started.postValue(true)
    }

    fun stop() {
        Log.d("TrackingViewModel", "Stop!")
        _started.postValue(false)
    }

    fun toggleMode() {
        Log.d("TrackingViewModel", "Mode Toggled")
        _mode.value = (if (mode.value == Mode.CYCLING) Mode.STEPS else Mode.CYCLING)
    }

    val broadcastStepReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val steps = intent.getIntExtra(StepDetectorService.KEY_STEP, 0)
            Log.d("Steps:", steps.toString())
        }
    }


}