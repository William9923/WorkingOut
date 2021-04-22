package com.softhouse.workingout.ui.sensor.tracker

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.shared.Polyline

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
    private val _startTime = MutableLiveData<Long>().apply {
        value = 0L
    }

    private val _endTime = MutableLiveData<Long>().apply {
        value = 0L
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
        _started.postValue(true)
    }

    fun stop() {
        _started.postValue(false)
    }

    fun toggleMode() {
        _mode.postValue(if (mode.value == Mode.CYCLING) Mode.STEPS else Mode.CYCLING)
    }

    fun updateTracking(newIsTraking: Boolean) {
        _isTracking.postValue(newIsTraking)
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
}