package com.softhouse.workingout.ui.sensor.tracker

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softhouse.workingout.data.db.Cycling
import com.softhouse.workingout.data.db.Running
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.shared.Polyline
import com.softhouse.workingout.shared.TrackingUtility
import kotlinx.coroutines.launch
import java.util.*

class TrackingViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    /**
     * Application Logic State
     */
    private val _started = MutableLiveData<Boolean>().apply {
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
    var mode: LiveData<Mode> = _mode

    var startTime: Long = 0L
    var endTime: Long = 0L

    /**
     * Tracking Application Logic
     */
    fun start() {
        _started.postValue(true)
        startTime = Calendar.getInstance().timeInMillis
    }

    fun stop() {
        _started.postValue(false)
        endTime = Calendar.getInstance().timeInMillis
        endWorkoutAndSave()
    }

    fun toggleMode() {
        _mode.postValue(if (mode.value == Mode.CYCLING) Mode.STEPS else Mode.CYCLING)
//        mainRepository.getAllCyclingSortedByDistanceInMeter().observeForever {
//            Log.d("Size", it.size.toString())
//            it.forEach { cycle -> Log.d("Cycle", cycle.toString()) }
//        }
    }

    fun updateDuration(newDuration: Long) {
        _duration.postValue(newDuration)
    }

    // Steps detector related
    fun updateSteps(newSteps: Int) {
        _steps.postValue(newSteps)
    }

    // Location - cycling detector related
    fun updateCoordinates(newCoordinates: Polyline) {
        _coordinates.postValue(newCoordinates)
    }

    /**
     * Persistence - state related function
     */

    private fun endWorkoutAndSave() {
        when (_mode.value) {
            Mode.STEPS -> endRunningWorkoutAndSave()
            Mode.CYCLING -> endCyclingWorkoutAndSave()
        }
    }

    private fun endRunningWorkoutAndSave() {
        Log.d("ViewModel", "Ending Running Workout")
        val running = Running(_steps.value!!)
        Log.d("Running", running.toString())
        // Show save result
        viewModelScope.launch {
            mainRepository.insertRunning(running)
        }
    }

    private fun endCyclingWorkoutAndSave() {
        Log.d("ViewModel", "Ending Cycling Workout")
        val distanceInMeters = TrackingUtility.calculatePolylineLength(_coordinates.value ?: mutableListOf()).toInt()
        val cycling = Cycling(distanceInMeters, _coordinates.value!!)
        Log.d("Cycling", cycling.toString())
        // Show save result
        viewModelScope.launch {
            mainRepository.insertCycling(cycling)
        }
    }
}