package com.softhouse.workingout.ui.log.list

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.data.db.Cycling
import com.softhouse.workingout.data.db.Running
import com.softhouse.workingout.data.repository.MainRepository
import com.softhouse.workingout.shared.Constants
import java.util.ArrayList

class RunningLogsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val _runnings = MutableLiveData<List<Running>>().apply {
        value = ArrayList()
    }

    private val _position = MutableLiveData<Long>().apply {
        value = Constants.INVALID_ID_DB
    }

    var day: Int = Constants.INVALID_DATE
    var month: Int = Constants.INVALID_MONTH
    var year: Int = Constants.INVALID_YEAR
    var records: LiveData<List<Running>> = _runnings
    var position: LiveData<Long> = _position

    fun initDate(day: Int, month: Int, year: Int) {
        if (this.day != day || this.month != month || this.year != year) {
            this.day = day
            this.month = month
            this.year = year
            initData()
        }
    }

    private fun initData() {
        if (day != Constants.INVALID_DATE && month != Constants.INVALID_MONTH && year != Constants.INVALID_YEAR) {
            mainRepository.getAllRunningRecordBasedOnDate(day, month, year).observeForever {
                Log.d("Data", it.toString())
                _runnings.postValue(it)
            }
        }
    }
}