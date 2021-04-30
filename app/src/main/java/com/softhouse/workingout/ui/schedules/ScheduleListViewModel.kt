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
    private fun initData() {
        mainRepository.getAllSchedule().observeForever {
            Log.d("Database", "Schedules data : " + it.toString())
            _schedules.postValue(it)
        }
    }

    // Delete
    private fun deleteData(id: Long) {
        val data = mainRepository.getSpecificScheduleById(id).value
        if (data != null) {
            viewModelScope.launch {
                with(mainRepository) {
                    Log.d("Database", "Delete Id : $id")
                    Log.d("Database", "Total item: ${_schedules.value?.size}")
                    deleteSchedule(data)
                    initData()
                    Log.d("Database", "Total item: ${_schedules.value?.size}")
                }
            }
        }
    }
}