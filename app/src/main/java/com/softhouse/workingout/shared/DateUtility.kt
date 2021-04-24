package com.softhouse.workingout.shared

import java.util.*

object DateUtility {

    fun getCalender(day: Int, month: Int, year: Int): Calendar {
        val calender = Calendar.getInstance()
        calender.set(year, month, day)
        return calender
    }
}