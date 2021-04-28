package com.softhouse.workingout.ui.sensor.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackingViewModel : ViewModel() {
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

    fun toggleMode() {
        _mode.value = when (_mode.value) {
            Mode.STEPS -> Mode.CYCLING
            Mode.CYCLING -> Mode.STEPS
            else -> Mode.STEPS
        }
    }

    fun initMode(defaultMode: Mode) {
        this._mode.value = defaultMode
    }
}