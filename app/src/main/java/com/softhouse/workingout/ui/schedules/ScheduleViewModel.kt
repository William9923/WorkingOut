package com.softhouse.workingout.ui.schedules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.ui.sensor.tracker.Mode

class ScheduleViewModel : ViewModel() {

    /**
     * Application Logic State
     */
    private val _mode = MutableLiveData<Mode>().apply {
        value = Mode.STEPS
    }

    private val _type = MutableLiveData<Types>().apply {
        value = Types.SINGLE
    }

    /**
     * Binding public variable
     */
    var mode: LiveData<Mode> = _mode
    var type: LiveData<Types> = _type

    fun toggleMode() {
        _mode.value = when (_mode.value) {
            Mode.STEPS -> Mode.CYCLING
            Mode.CYCLING -> Mode.STEPS
            else -> Mode.STEPS
        }
    }

    fun toggleType() {
        _type.value = when (_type.value) {
            Types.SINGLE -> Types.REPEATING
            Types.REPEATING -> Types.REPEATING_WEEK
            Types.REPEATING_WEEK -> Types.SINGLE
            else -> Types.SINGLE
        }
    }

    fun initMode(defaultMode: Mode = Mode.STEPS) {
        _mode.value = defaultMode
    }

    fun initType(defaultType: Types = Types.SINGLE) {
        _type.value = defaultType

    }
}