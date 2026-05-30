package com.example.pulse_healthtracker

import java.util.Date

data class CalendarDate(
    val date: Date?,
    var isSelected: Boolean = false,
    var isToday: Boolean = false
)
