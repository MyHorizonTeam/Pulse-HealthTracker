package com.example.pulse_healthtracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class PrivacyActivity : AppCompatActivity() {

    private lateinit var sharedPref: android.content.SharedPreferences

    private lateinit var switchShareActivity: Switch
    private lateinit var switchShareSleep:    Switch
    private lateinit var switchShareHealth:   Switch
    private lateinit var switchAppLock:       Switch
    private lateinit var switchTwoFactor:     Switch
    private lateinit var btnExportData:       Button
    private lateinit var btnClearData:        Button
    private lateinit var btnSavePrivacy:      Button
    private lateinit var btnBack:             Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Bind views
        switchShareActivity = findViewById(R.id.switchShareActivity)
        switchShareSleep    = findViewById(R.id.switchShareSleep)
        switchShareHealth   = findViewById(R.id.switchShareHealth)
        switchAppLock       = findViewById(R.id.switchAppLock)
        switchTwoFactor     = findViewById(R.id.switchTwoFactor)
        btnExportData       = findViewById(R.id.btnExportData)
        btnClearData        = findViewById(R.id.btnClearData)
        btnSavePrivacy      = findViewById(R.id.btnSavePrivacy)
        btnBack             = findViewById(R.id.btnBack)

        loadPrivacySettings()

        btnBack.setOnClickListener { finish() }

        btnSavePrivacy.setOnClickListener { savePrivacySettings() }

        btnExportData.setOnClickListener {
            Toast.makeText(this,
                "Your data export will be sent to your email",
                Toast.LENGTH_LONG).show()
        }

        btnClearData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear all data")
                .setMessage("This will permanently delete all your health data. Are you sure?")
                .setPositiveButton("Clear") { _, _ ->
                    with(sharedPref.edit()) {
                        remove("weight")
                        remove("height")
                        remove("age")
                        remove("steps")
                        remove("sleep_hr")
                        remove("bmi")
                        apply()
                    }
                    Toast.makeText(this, "All health data cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun loadPrivacySettings() {
        switchShareActivity.isChecked = sharedPref.getBoolean("privacy_activity", true)
        switchShareSleep.isChecked    = sharedPref.getBoolean("privacy_sleep", false)
        switchShareHealth.isChecked   = sharedPref.getBoolean("privacy_health", false)
        switchAppLock.isChecked       = sharedPref.getBoolean("privacy_applock", false)
        switchTwoFactor.isChecked     = sharedPref.getBoolean("privacy_2fa", false)
    }

    private fun savePrivacySettings() {
        with(sharedPref.edit()) {
            putBoolean("privacy_activity", switchShareActivity.isChecked)
            putBoolean("privacy_sleep",    switchShareSleep.isChecked)
            putBoolean("privacy_health",   switchShareHealth.isChecked)
            putBoolean("privacy_applock",  switchAppLock.isChecked)
            putBoolean("privacy_2fa",      switchTwoFactor.isChecked)
            apply()
        }
        Toast.makeText(this, "Privacy settings saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}