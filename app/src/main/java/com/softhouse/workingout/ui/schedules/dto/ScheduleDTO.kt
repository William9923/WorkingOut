package com.softhouse.workingout.ui.schedules.dto

data class ScheduleDTO(
    var id: Long,
    var startTime: String,
    var endTime: String,
    var desc: String,
    var detail: String,
    var autoStart: Boolean
)
