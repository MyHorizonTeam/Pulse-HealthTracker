package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Breakfast : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_breakfast)


        val mealHeader = findViewById<TextView>(R.id.tvMealHeader)
        val layoutBreakfast = findViewById<LinearLayout>(R.id.layoutBreakfast)


        val mealType = intent.getStringExtra("MEAL_TYPE") ?: "Breakfast"
        mealHeader.text = mealType


        layoutBreakfast.visibility =
            if (mealType == "Breakfast") View.VISIBLE else View.GONE


        val deleteEggsBtn = findViewById<ImageView>(R.id.imgDeleteEggs)
        val eggsCard = findViewById<CardView>(R.id.cardEggs)

        deleteEggsBtn.setOnClickListener {
            eggsCard.visibility = View.GONE
        }
        val toastCard = findViewById<CardView>(R.id.cardToast)
        val avocadoCard = findViewById<CardView>(R.id.cardAvocado)

        val deleteToast = findViewById<ImageView>(R.id.imgDeleteToast)
        val deleteAvocado = findViewById<ImageView>(R.id.imgDeleteAvocado)

        deleteToast.setOnClickListener {
            toastCard.visibility = View.GONE
        }

        deleteAvocado.setOnClickListener {
            avocadoCard.visibility = View.GONE
        }

        val editMealBtn = findViewById<Button>(R.id.btnEditMeal)
        val closeBtn = findViewById<Button>(R.id.btnClose)

        editMealBtn.setOnClickListener {
            val intent = Intent(this, EditMealActivity::class.java)
            startActivity(intent)
        }

        closeBtn.setOnClickListener {
            finish()
        }
    }
}