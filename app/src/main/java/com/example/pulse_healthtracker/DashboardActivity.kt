package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnAddMeal = findViewById<Button>(R.id.btnAddMeal)
        val cardBreakfast = findViewById<CardView>(R.id.cardBreakfast)


        btnProfile.setOnClickListener {

        }


        btnAddMeal.setOnClickListener {
            val intent = Intent(this, AddMealActivity::class.java)
            startActivity(intent)
        }


        cardBreakfast.setOnClickListener {
            val intent = Intent(this, Breakfast::class.java)
            intent.putExtra("MEAL_TYPE", "Breakfast")
            startActivity(intent)
        }


        val cardLunch = findViewById<CardView>(R.id.cardLunch) // ඔබ XML එකේ දාපු Lunch කාඩ් එකේ ID එක මෙතනට දමන්න

        cardLunch.setOnClickListener {
            val intent = Intent(this, LunchActivity::class.java) // ඔබ සාදාගත් LunchActivity එක
            intent.putExtra("MEAL_TYPE", "Lunch")
            startActivity(intent)
        }
    }
}