package com.softhouse.workingout.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunningDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunning(running: Running): Long

    @Delete
    suspend fun deleteRunning(running: Running)

    @Query("SELECT * FROM running_table")
    fun getAllRunningRecord(): LiveData<List<Running>>

    @Query("SELECT * FROM running_table ORDER BY steps DESC")
    fun getAllRunningSortedBySteps(): LiveData<List<Running>>

    @Query("SELECT * FROM running_table WHERE start_workout BETWEEN :dayStart AND :dayEnd")
    fun getAllRunningBasedOnDate(dayStart: Long, dayEnd: Long): LiveData<List<Running>>

    @Query("SELECT * FROM running_table WHERE id = :id LIMIT 1")
    fun getSpecificRunningById(id: Long): LiveData<Running>

    @Query("SELECT SUM(steps) FROM running_table")
    fun getTotalSteps(): LiveData<Int>
}