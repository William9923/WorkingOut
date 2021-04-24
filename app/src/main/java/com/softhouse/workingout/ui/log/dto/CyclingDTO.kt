package com.softhouse.workingout.ui.log.dto

data class CyclingDTO(
    var id: Long,
    var distance: Int,
    var startWorkout: String,
    var endWorkout: String,
    var duration: String
)
