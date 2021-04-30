package com.softhouse.workingout.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedule_table")
    fun getAllSchedule(): LiveData<List<Schedule>>

    @Query("SELECT * FROM schedule_table WHERE id = :id LIMIT 1")
    fun getSpecificScheduleById(id: Long): LiveData<Schedule>

}