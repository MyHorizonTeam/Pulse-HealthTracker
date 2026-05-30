package com.example.pulse_healthtracker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ReminderActivity : AppCompatActivity() {

    private lateinit var tvPillName: TextView
    private lateinit var tvPillDesc: TextView
    private lateinit var cardMedicine1: CardView
    private lateinit var cardMedicine2: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reminder)
        
        initViews()
        loadReminderData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        tvPillName = findViewById(R.id.tvPillName1)
        tvPillDesc = findViewById(R.id.tvPillDesc1)
        cardMedicine1 = findViewById(R.id.cardMedicine1)
        cardMedicine2 = findViewById(R.id.cardMedicine2)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun loadReminderData() {
        val pillName = intent.getStringExtra("pillName")
        val dose = intent.getStringExtra("dose")
        val time = intent.getStringExtra("time")

        if (pillName != null) {
            tvPillName.text = pillName
            tvPillDesc.text = "$dose pills  |  $time"
            cardMedicine1.visibility = View.VISIBLE
            cardMedicine2.visibility = View.GONE // Hide the placeholder second card
        }
    }
}
