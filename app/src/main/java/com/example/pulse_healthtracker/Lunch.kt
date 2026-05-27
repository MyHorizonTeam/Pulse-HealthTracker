package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class LunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)


        val btnDeleteChicken = findViewById<ImageView>(R.id.imgDeleteChicken)
        val cardChicken = findViewById<CardView>(R.id.cardChicken)


        val btnDeleteBeef = findViewById<ImageView>(R.id.imgDeleteBeef)
        val cardBeef = findViewById<CardView>(R.id.cardBeef)


        val btnDeleteBroccoli = findViewById<ImageView>(R.id.imgDeleteBroccoli)
        val cardBroccoli = findViewById<CardView>(R.id.cardBroccoli)


        btnDeleteChicken.setOnClickListener { cardChicken.visibility = View.GONE }
        btnDeleteBeef.setOnClickListener { cardBeef.visibility = View.GONE }
        btnDeleteBroccoli.setOnClickListener { cardBroccoli.visibility = View.GONE }

        val btnEditMeal = findViewById<Button>(R.id.btnEditMeal)
        val btnClose = findViewById<Button>(R.id.btnClose)

        btnEditMeal.setOnClickListener {
            val intent = Intent(this, EditMealActivity::class.java)
            startActivity(intent)
        }

        btnClose.setOnClickListener {
            finish()
        }
    }
}