package com.softhouse.workingout.ui.schedules

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softhouse.workingout.data.db.Schedule
import com.softhouse.workingout.data.repository.MainRepository
import kotlinx.coroutines.launch
import java.util.*

class ScheduleListViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val _schedules = MutableLiveData<List<Schedule>>().apply {
        value = ArrayList()
    }

    var schedules: LiveData<List<Schedule>> = _schedules

    // Init
    fun initData() {
        mainRepository.getAllSchedule().observeForever {
            Log.d("Database", "Schedules data : $it")
            _schedules.postValue(it)
        }
    }

    // Delete
    fun deleteData(data: Schedule) {
        if (data != null) {
            viewModelScope.launch {
                with(mainRepository) {
                    Log.d("Database", "Total item: ${_schedules.value?.size}")
                    deleteSchedule(data)
                    initData()
                    Log.d("Database", "Total item: ${_schedules.value?.size}")
                }
            }
        }
    }
}