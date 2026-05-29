package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NutStartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nut_start)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        val txtLoginLink = findViewById<TextView>(R.id.txtLoginLink)


        btnGetStarted.setOnClickListener {
            Toast.makeText(this, "Redirecting to Dashboard...", Toast.LENGTH_SHORT).show()


            val intent = Intent(this, DashboardActivity_Nut::class.java)
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