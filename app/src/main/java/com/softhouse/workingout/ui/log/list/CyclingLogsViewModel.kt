package com.softhouse.workingout.ui.log.list

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.data.db.Cycling
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.shared.Constants

class CyclingLogsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val _cyclings = MutableLiveData<List<Cycling>>().apply {
        value = null
    }

    private val _position = MutableLiveData<Long>().apply {
        value = Constants.INVALID_ID_DB
    }

    var day: Int = Constants.INVALID_DATE
    var month: Int = Constants.INVALID_MONTH
    var year: Int = Constants.INVALID_YEAR
    var cycling: LiveData<List<Cycling>> = _cyclings
    var position: LiveData<Long> = _position

    fun initDate(day: Int, month: Int, year: Int) {
        this.day = day
        this.month = month
        this.year = year
    }

    fun initData(id: Long) {
        if (day != Constants.INVALID_DATE && month != Constants.INVALID_MONTH && year != Constants.INVALID_YEAR) {
            mainRepository.getAllCyclingRecordBasedOnDate(day, month, year).observeForever {
                Log.d("Data", it.toString())
                _cyclings.postValue(it)
            }
        }
    }
}