package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NutStartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_nut_start)

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        val txtLoginLink = findViewById<TextView>(R.id.txtLoginLink)

        btnGetStarted.setOnClickListener {
            Toast.makeText(this, "Redirecting to Dashboard...", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtLoginLink.setOnClickListener {
            Toast.makeText(this, "Redirecting to Login...", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}