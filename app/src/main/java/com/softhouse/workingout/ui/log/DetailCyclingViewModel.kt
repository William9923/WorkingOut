package com.softhouse.workingout.ui.log

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.data.db.Cycling
import com.softhouse.workingout.data.db.Running
import com.softhouse.workingout.data.repository.MainRepository

class DetailCyclingViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
    private val _cycling = MutableLiveData<Cycling>().apply {
        value = null
    }

    var cycling: LiveData<Cycling> = _cycling

    fun initData(id: Long) {
        mainRepository.getSpecificCyclingById(id).observeForever {
            _cycling.postValue(it)
        }
    }
}