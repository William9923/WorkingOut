package com.softhouse.workingout.ui.sensor.tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.service.GeoTrackerService
import com.softhouse.workingout.service.Polyline
import com.softhouse.workingout.service.StepDetectorService

class TrackingViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
    /**
     * Application Logic State
     */
    private val _started = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _isTracking = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _steps = MutableLiveData<Int>().apply {
        value = 0
    }

    private val _coordinates = MutableLiveData<Polyline>().apply {
        value = mutableListOf()
    }

    private val _mode = MutableLiveData<Mode>().apply {
        value = Mode.STEPS
    }

    private val _duration = MutableLiveData<Long>().apply {
        value = 0L
    }

    /**
     * Binding public variable
     */
    var started: LiveData<Boolean> = _started
    var isTracking: LiveData<Boolean> = _isTracking
    var steps: LiveData<Int> = _steps
    var coordinates: LiveData<Polyline> = _coordinates
    var mode: LiveData<Mode> = _mode
    var duration: LiveData<Long> = _duration

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
        _mode.postValue(if (mode.value == Mode.CYCLING) Mode.STEPS else Mode.CYCLING)
    }

    fun updateTracking(isTracking: Boolean) {
        _isTracking.postValue(isTracking)
    }

    fun updateSteps(newSteps: Int) {
        _steps.postValue(newSteps)
    }

    fun updateCoordinates(newCoordinates: Polyline) {
        _coordinates.postValue(newCoordinates)
    }

    fun updateDuration(newDuration: Long) {
        _duration.postValue(newDuration)
    }

//    val broadcastStepReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val steps = intent.getIntExtra(StepDetectorService.KEY_STEP, 0)
//            Log.d("Steps:", steps.toString())
//        }
//    }
}