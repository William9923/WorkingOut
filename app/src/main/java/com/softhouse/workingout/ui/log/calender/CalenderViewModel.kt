package com.softhouse.workingout.ui.log.calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.ui.sensor.tracker.Mode

class CalenderViewModel : ViewModel() {
    /**
     * Application Logic State
     */
    private val _mode = MutableLiveData<Mode>().apply {
        value = Mode.STEPS
    }

    /**
     * Binding public variable
     */
    var mode: LiveData<Mode> = _mode

    fun changeMode() {
        // state design pattern
        _mode.value = when (_mode.value) {
            Mode.STEPS -> Mode.CYCLING
            Mode.CYCLING -> Mode.STEPS
            else -> Mode.STEPS
        }
    }
}