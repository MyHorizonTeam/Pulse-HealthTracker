package com.example.pulse_healthtracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditMealActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meal)

        val etMealName = findViewById<EditText>(R.id.etMealName)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val newName = etMealName.text.toString()
            val newCals = etCalories.text.toString()


            Toast.makeText(this, "Meal updated: $newName ($newCals cal)", Toast.LENGTH_SHORT).show()


            finish()
        }
    }
}