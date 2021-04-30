package com.softhouse.workingout.ui.schedules

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softhouse.workingout.data.db.Schedule
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.service.StartScheduleService
import com.softhouse.workingout.service.StopScheduleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class ScheduleViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private lateinit var startScheduleService: StartScheduleService
    private lateinit var stopScheduleService: StopScheduleService

    private val dayToInt = mutableMapOf(
        "Mon" to 1,
        "Tues" to 2,
        "Wed" to 3,
        "Thurs" to 4,
        "Fri" to 5,
        "Sat" to 6,
        "Sun" to 7,
    )

    fun initService(startScheduleService: StartScheduleService, stopScheduleService: StopScheduleService) {
        this.startScheduleService = startScheduleService
        this.stopScheduleService = stopScheduleService
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val id = mainRepository.insertSchedule(schedule)
            Log.d("Database", "New Schedule ID : $id")
            when (schedule.types) {
                Types.SINGLE -> {
                    startScheduleService.setSingleAlarm(schedule.startTime, schedule.mode, id, schedule.autoStart)
                    stopScheduleService.setSingleAlarm(schedule.stopTime, schedule.mode, id, schedule.autoStart)
                }
                Types.REPEATING -> {
                    startScheduleService.setRepeatingAlarm(schedule.startTime, schedule.mode, id, schedule.autoStart)
                    stopScheduleService.setRepeatingAlarm(schedule.stopTime, schedule.mode, id, schedule.autoStart)
                }
                else -> {
                    val listOfStartMillis = mutableListOf<Long>()
                    val listOfStopMillis = mutableListOf<Long>()

                    val startCalendar = Calendar.getInstance()
                    val endCalendar = Calendar.getInstance()
                    startCalendar.timeInMillis = schedule.startTime
                    endCalendar.timeInMillis = schedule.stopTime

                    for (day in schedule.weeks) {
                        if (dayToInt.containsKey(day)) {
                            val idx = dayToInt[day]
                            val start = Calendar.getInstance()
                            val end = Calendar.getInstance()
                            val now = Calendar.getInstance().timeInMillis
                            start.set(Calendar.DAY_OF_WEEK, idx ?: 1)
                            end.set(Calendar.DAY_OF_WEEK, idx ?: 1)

                            start.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY))
                            start.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE))
                            start.set(Calendar.SECOND, startCalendar.get(Calendar.SECOND))

                            end.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY))
                            end.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE))
                            end.set(Calendar.SECOND, endCalendar.get(Calendar.SECOND))

                            if (start.timeInMillis < now) {
                                start.timeInMillis = start.timeInMillis + TimeUnit.DAYS.toMillis(7)
                                end.timeInMillis = end.timeInMillis + TimeUnit.DAYS.toMillis(7)
                            }

                            listOfStartMillis.add(start.timeInMillis)
                            listOfStopMillis.add(end.timeInMillis)

                        }
                    }

                    startScheduleService.setRepeatingWeeksAlarm(
                        listOfStartMillis,
                        schedule.mode,
                        id,
                        schedule.autoStart
                    )
                    stopScheduleService.setRepeatingWeeksAlarm(listOfStopMillis, schedule.mode, id, schedule.autoStart)
                }

            }
        }
    }
}