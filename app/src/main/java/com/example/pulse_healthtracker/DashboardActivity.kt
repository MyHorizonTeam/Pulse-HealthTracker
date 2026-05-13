package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pulse_healthtracker.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Navigation is handled by DashboardFragment using NavHostFragment
    }
}