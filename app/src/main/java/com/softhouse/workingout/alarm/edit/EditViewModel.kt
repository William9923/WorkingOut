package com.softhouse.workingout.alarm.edit

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.softhouse.androidutils.Event
import com.softhouse.androidutils.Result
import com.softhouse.workingout.R
import com.softhouse.workingout.alarm.calculateDurationString
import com.softhouse.workingout.alarm.cancel
import com.softhouse.workingout.data.Alarm
//import com.softhouse.workingout.music.data.Track
import com.softhouse.workingout.data.repository.AlarmRepository
//import com.softhouse.workingout.repository.MusicRepository
import com.softhouse.workingout.alarm.schedule
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

class EditViewModel(
    private val alarmRepository: AlarmRepository,
//    private val musicRepository: MusicRepository,
    private val app: Application
) :
    AndroidViewModel(app) {

    companion object {
        private const val INITIAL_TIME_INTERVAL = 3000L
        private const val TIME_INTERVAL = 10000L
    }

    private val _goToMainPageEvent = MutableLiveData<Event<Unit>>()
    val goToMainPageEvent: LiveData<Event<Unit>> get() = _goToMainPageEvent

    private val _alarmDateTime = MutableLiveData<LocalDateTime?>()
    val alarmDateTime: LiveData<LocalDateTime?> = _alarmDateTime

    private val _selectedAlarm = MutableLiveData<Alarm>()
    val selectedAlarm: LiveData<Alarm> = _selectedAlarm
    var newAlarm = false
        private set

//    private val _track = MutableLiveData<Track?>()
//    val track: LiveData<Track?> = _track

    private val handler = Handler(Looper.getMainLooper())

    private val tickRunnable = object : Runnable {
        override fun run() {
            _alarmDateTime.value = _selectedAlarm.value?.nearestDateTime()
            handler.postDelayed(this, TIME_INTERVAL)
        }
    }

    init {
        handler.postDelayed(tickRunnable, INITIAL_TIME_INTERVAL)
    }

    fun retrieveAlarm(alarmId: Int?) {
        var alarm: Alarm
        viewModelScope.launch {
            if (alarmId == null || alarmId == -1) {
                alarm = defaultAlarm()
                newAlarm = true
            } else {
                val result = alarmRepository.getAlarmById(alarmId)
                if (result is Result.Success) {
                    alarm = result.data
                    newAlarm = false
                } else {
                    alarm = defaultAlarm()
                    newAlarm = true
                }
            }
            _selectedAlarm.value = alarm.apply { enabled = true }
            _alarmDateTime.value = alarm.nearestDateTime()
//            if (alarm.trackId == "") {
//                _track.value = null
//            } else {
//                val result = musicRepository.getTrack(alarm.trackId)
//                _track.value = (result as? Result.Success)?.data
//            }
        }
    }

    fun updateAlarmTime(hour: Int, minute: Int) {
        selectedAlarm.value?.apply {
            this.hour = hour
            this.minute = minute
            _alarmDateTime.value = nearestDateTime()
        }
    }

    fun updateAlarmDay(day: Int, isChecked: Boolean) {
        selectedAlarm.value?.apply {
            days = if (isChecked) {
                days or (1 shl day)
            } else {
                days and (1 shl day).inv()
            }
            _alarmDateTime.value = nearestDateTime()
        }
    }

    fun updateAlarmDay(selectedDays: Int) {
        selectedAlarm.value?.apply {
            days = selectedDays
            _alarmDateTime.value = nearestDateTime()
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            selectedAlarm.value?.let {
                it.cancel(getApplication())
                alarmRepository.deleteAlarm(it)
            }
            _goToMainPageEvent.value = Event(Unit)
        }
    }

    fun onSaveClicked(description: String, vibrate: Boolean, snooze: Boolean) {
        val alarm = selectedAlarm.value
        if (alarm == null) {
            _goToMainPageEvent.value = Event(Unit)
            return
        }
        alarm.description = description
        alarm.vibrate = vibrate
        alarm.snooze = snooze
        viewModelScope.launch {
            if (newAlarm) {
                alarmRepository.insertAlarm(alarm)
            } else {
                alarmRepository.updateAlarm(alarm)
                alarm.cancel(getApplication())
            }
            val date = alarm.schedule(getApplication())
            date?.let {
                val text =
                    app.getString(R.string.alarm_go_off) + " " + calculateDurationString(app, date)
                Toast.makeText(app, text, Toast.LENGTH_SHORT).show()
            }
            _goToMainPageEvent.value = Event(Unit)
        }
    }

    fun onMusicFabClicked() {
//        _goToMusicPageEvent.value = Event(Unit)
    }

    fun onTrackUpdated(trackId: String) {
        viewModelScope.launch {
//            if (trackId == "") {
//                _track.value = null
//            } else {
//                val result = musicRepository.getTrack(trackId)
//                _track.value = (result as? Result.Success)?.data
//                _selectedAlarm.value?.trackId = trackId
//                _selectedAlarm.value?.trackUrl = _track.value?.previewURL ?: ""
//                _selectedAlarm.value?.albumId = _track.value?.albumId ?: ""
//            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(tickRunnable)
    }

    private fun defaultAlarm(): Alarm {
        val now = LocalTime.now()
        return Alarm(
            now.hour,
            now.minute,
            false,
            Alarm.ONCE,
            vibrate = false,
            snooze = true
        )
    }
}
