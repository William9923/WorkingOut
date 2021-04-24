package com.softhouse.workingout.ui.log.dto

data class RunningDTO(
    var id: Long,
    var steps: Int,
    var startWorkout: String,
    var endWorkout: String,
    var duration: Long
)
