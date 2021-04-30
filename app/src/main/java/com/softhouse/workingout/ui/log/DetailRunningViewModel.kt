package com.softhouse.workingout.ui.log

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.data.db.Running
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.ui.sensor.tracker.Mode

class DetailRunningViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val _running = MutableLiveData<Running>().apply {
        value = null
    }

    var running: LiveData<Running> = _running

    fun initData(id: Long) {

        mainRepository.getSpecificRunningById(id).observeForever {
            _running.postValue(it)
        }
    }
}