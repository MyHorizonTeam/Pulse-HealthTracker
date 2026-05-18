package com.example.pulse_healthtracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddMealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_meal)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnSaveMeal = findViewById<Button>(R.id.btnSaveMeal)


        btnBack.setOnClickListener {
            finish()
        }


        btnSaveMeal.setOnClickListener {
            Toast.makeText(this, "Meal Saved Successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}