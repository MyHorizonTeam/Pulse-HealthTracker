package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import android.content.Intent

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val regSwitch=findViewById<Button>(R.id.regBtn)
        regSwitch.setOnClickListener{
            val intent = Intent(this,Register::class.java )
        }
        var email_u = findViewById<EditText>(R.id.edtxtEmail)
        var pwd = findViewById<EditText>(R.id.edtxtPwd)
        val lgBtn = findViewById<Button>(R.id.btnLog)
        val frgpwd = findViewById<TextView>(R.id.frgpwd)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}