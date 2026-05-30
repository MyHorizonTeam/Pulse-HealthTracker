package com.example.pulse_healthtracker

import com.google.firebase.firestore.PropertyName

data class Medicine(
    var medicineId: String = "",
    var pillName: String = "",
    var diseases: String = "",
    var dose: Double = 0.0,
    var date: String = "",
    var time: String = "",
    var foodRelation: String = "",
    var notes: String = "",
    var medicineTimes: List<String> = emptyList(),
    var createdAt: Long = 0,
    var status: String = "active",
    var userId: String = "",
    var imageUrl: String = "",
    
    @get:PropertyName("isTaken")
    @set:PropertyName("isTaken")
    var isTaken: Boolean = false
)
