package com.softhouse.workingout.ui.schedules

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softhouse.workingout.data.db.Cycling
import com.softhouse.workingout.data.db.Schedule
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.service.StartScheduleService
import com.softhouse.workingout.service.StopScheduleService
import com.softhouse.workingout.shared.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ScheduleViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val _scheduleId = MutableLiveData<Long>().apply {
        value = Constants.INVALID_ID_DB
    }

    var id: LiveData<Long> = _scheduleId

    lateinit var startScheduleService: StartScheduleService
    lateinit var stopScheduleService: StopScheduleService

    fun initService(startScheduleService: StartScheduleService, stopScheduleService: StopScheduleService) {
        this.startScheduleService = startScheduleService
        this.stopScheduleService = stopScheduleService
    }

    fun resetSchedule() {
        _scheduleId.value = Constants.INVALID_ID_DB
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val id = mainRepository.insertSchedule(schedule)
            // TODO: Masukin id , jadi extra di intent
            when (schedule.types) {
                Types.SINGLE -> {
                    startScheduleService.setSingleAlarm(schedule.startTime, schedule.mode, schedule.autoStart)
                    stopScheduleService.setSingleAlarm(schedule.stopTime, schedule.mode, schedule.autoStart)
                }
                Types.REPEATING -> {
                    startScheduleService.setRepeatingAlarm(schedule.startTime, schedule.mode, schedule.autoStart)
                    stopScheduleService.setRepeatingAlarm(schedule.stopTime, schedule.mode, schedule.autoStart)
                }
                else -> {
                    // TODO : 
                }

            }
            resetSchedule()
        }
    }
}