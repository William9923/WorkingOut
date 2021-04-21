package com.softhouse.workingout.ui.sensor.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackingViewModel : ViewModel() {
    private val _started = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _steps = MutableLiveData<Int>().apply {
        value = 0
    }

    private val _mode = MutableLiveData<Mode>().apply {
        value = Mode.STEPS
    }

    var started: LiveData<Boolean> = _started
    var steps: LiveData<Int> = _steps
    var mode: LiveData<Mode> = _mode

    // TODO : Make sure view model only created once in activity : https://stackoverflow.com/questions/56378562/viewmodel-refetches-data-when-fragment-is-recreated
}