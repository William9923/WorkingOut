package com.softhouse.workingout.data.repository

import androidx.lifecycle.LiveData
import com.softhouse.workingout.data.db.*
import com.softhouse.workingout.shared.DateUtility
import com.softhouse.workingout.ui.sensor.tracker.Mode
import java.util.*
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runningDao: RunningDao,
    val cyclingDao: CyclingDao
) {

    // Insert
    suspend fun insertCycling(cycling: Cycling) = cyclingDao.insertCycling(cycling)
    suspend fun insertRunning(running: Running) = runningDao.insertRunning(running)

    // Query based on date:
    fun getAllCyclingRecordBasedOnDate(day: Int, month: Int, year: Int): LiveData<List<Cycling>> {
        val calender = DateUtility.getCalender(day, month, year)
        val nextDay = DateUtility.getCalender(day, month, year)
        nextDay.add(Calendar.DAY_OF_MONTH, 1)
        return cyclingDao.getAllCyclingBasedOnDate(calender.timeInMillis, nextDay.timeInMillis)
    }

    fun getAllRunningRecordBasedOnDate(day: Int, month: Int, year: Int): LiveData<List<Running>> {
        val calender = DateUtility.getCalender(day, month, year)
        val nextDay = DateUtility.getCalender(day, month, year)
        nextDay.add(Calendar.DAY_OF_MONTH, 1)
        return runningDao.getAllRunningBasedOnDate(calender.timeInMillis, nextDay.timeInMillis)
    }

    // Query One Specific Result
    fun getSpecificCyclingById(id: Int) = cyclingDao.getSpecificCyclingById(id)
    fun getSpecificRunningById(id: Int) = runningDao.getSpecificRunningById(id)

    // Query Aggregated Result
    fun getTotalDistance() = cyclingDao.getTotalDistance()
    fun getTotalSteps() = runningDao.getTotalSteps()
}