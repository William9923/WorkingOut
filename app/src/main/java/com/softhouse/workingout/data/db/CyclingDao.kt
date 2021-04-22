package com.softhouse.workingout.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CyclingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycling(cycling: Cycling)

    @Delete
    suspend fun deleteCycling(cycling: Cycling)

    @Query("SELECT * FROM cycling_table WHERE id = :id LIMIT 1")
    fun getSpecificCyclingById(id: Int): LiveData<Cycling>

    @Query("SELECT * FROM cycling_table ORDER BY distanceInMeters DESC")
    fun getAllCyclingSortedByDistanceInMeter(): LiveData<List<Cycling>>

    @Query("SELECT SUM(distanceInMeters) FROM cycling_table")
    fun getTotalDistance(): LiveData<Int>
}