package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        // Hide the status bar for full screen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val logoImage = findViewById<ImageView>(R.id.logoImage)

        // Load and start animation
        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        logoImage.startAnimation(animation)

        // Navigate after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({

            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isFirstTime = prefs.getBoolean("first_time", true)

            if (isFirstTime) {
                prefs.edit().putBoolean("first_time", false).apply()
                startActivity(Intent(this, Onboarding::class.java))
            } else {
                // val user = FirebaseAuth.getInstance().currentUser
                /* if (user != null) {
                     startActivity(Intent(this, HomeActivity::class.java))
                 } else {
                     startActivity(Intent(this, LoginActivity::class.java))
                 } */
                startActivity(Intent(this, Login::class.java))
            }
            finish()

        }, 3000)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}