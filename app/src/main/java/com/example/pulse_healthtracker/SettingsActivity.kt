package com.example.pulse_healthtracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: android.content.SharedPreferences

    private lateinit var switchDailyReminder:  Switch
    private lateinit var switchSleepReminder:  Switch
    private lateinit var switchAchievement:    Switch
    private lateinit var spinnerDistance:      Spinner
    private lateinit var spinnerWeight:        Spinner
    private lateinit var spinnerTemp:          Spinner
    private lateinit var btnChangePassword:    Button
    private lateinit var btnDeleteAccount:     Button
    private lateinit var btnSaveSettings:      Button
    private lateinit var btnBack:              Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Bind views
        switchDailyReminder = findViewById(R.id.switchDailyReminder)
        switchSleepReminder = findViewById(R.id.switchSleepReminder)
        switchAchievement   = findViewById(R.id.switchAchievement)
        spinnerDistance     = findViewById(R.id.spinnerDistance)
        spinnerWeight       = findViewById(R.id.spinnerWeight)
        spinnerTemp         = findViewById(R.id.spinnerTemp)
        btnChangePassword   = findViewById(R.id.btnChangePassword)
        btnDeleteAccount    = findViewById(R.id.btnDeleteAccount)
        btnSaveSettings     = findViewById(R.id.btnSaveSettings)
        btnBack             = findViewById(R.id.btnBack)

        setupSpinners()
        loadSettings()

        btnBack.setOnClickListener { finish() }

        btnSaveSettings.setOnClickListener { saveSettings() }

        btnChangePassword.setOnClickListener {
            val email = auth.currentUser?.email ?: return@setOnClickListener
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,
                            "Password reset email sent to $email",
                            Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    auth.currentUser?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,
                                    "Account deleted",
                                    Toast.LENGTH_SHORT).show()
                                startActivity(android.content.Intent(
                                    this, Login::class.java))
                                finishAffinity()
                            } else {
                                Toast.makeText(this,
                                    "Error: ${task.exception?.message}",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupSpinners() {
        spinnerDistance.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("km", "miles"))

        spinnerWeight.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("kg", "lbs"))

        spinnerTemp.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("°C", "°F"))
    }

    private fun loadSettings() {
        switchDailyReminder.isChecked = sharedPref.getBoolean("notif_daily", true)
        switchSleepReminder.isChecked = sharedPref.getBoolean("notif_sleep", true)
        switchAchievement.isChecked   = sharedPref.getBoolean("notif_achv", false)
        spinnerDistance.setSelection(sharedPref.getInt("unit_distance", 0))
        spinnerWeight.setSelection(sharedPref.getInt("unit_weight", 0))
        spinnerTemp.setSelection(sharedPref.getInt("unit_temp", 0))
    }

    private fun saveSettings() {
        with(sharedPref.edit()) {
            putBoolean("notif_daily", switchDailyReminder.isChecked)
            putBoolean("notif_sleep", switchSleepReminder.isChecked)
            putBoolean("notif_achv",  switchAchievement.isChecked)
            putInt("unit_distance",   spinnerDistance.selectedItemPosition)
            putInt("unit_weight",     spinnerWeight.selectedItemPosition)
            putInt("unit_temp",       spinnerTemp.selectedItemPosition)
            apply()
        }
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}