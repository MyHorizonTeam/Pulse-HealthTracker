package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pulse_healthtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Navigation is now handled by NavHostFragment in activity_main.xml
    }
}
