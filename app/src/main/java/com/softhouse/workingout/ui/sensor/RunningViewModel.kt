package com.softhouse.workingout.ui.sensor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RunningViewModel : ViewModel() {
    private val _started = MutableLiveData<Boolean>().apply {
        value = false
    }
    val started: LiveData<Boolean> = _started
}