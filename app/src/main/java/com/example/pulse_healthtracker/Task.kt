package com.example.pulse_healthtracker

data class Task(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    val description: String = "",
    val time: String = "",
    var isCompleted: Boolean = false,
    val color: String = "#7E57C2"
)