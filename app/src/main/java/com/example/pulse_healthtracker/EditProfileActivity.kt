package com.example.pulse_healthtracker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: android.content.SharedPreferences

    // Views
    private lateinit var etFullName:    EditText
    private lateinit var etEmail:       EditText
    private lateinit var etPhone:       EditText
    private lateinit var etBio:         EditText
    private lateinit var etAge:         EditText
    private lateinit var etHeight:      EditText
    private lateinit var etWeight:      EditText
    private lateinit var etCalories:    EditText
    private lateinit var etConditions:  EditText
    private lateinit var spinnerGender:     Spinner
    private lateinit var spinnerBloodType:  Spinner
    private lateinit var spinnerActivity:   Spinner
    private lateinit var btnSave:       Button
    private lateinit var btnBack:       Button
    private lateinit var btnChangePic:  Button
    private lateinit var imgProfile:    android.widget.ImageView
    private lateinit var cardProfile:   CardView

    companion object {
        private const val PICK_IMAGE = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Bind views
        etFullName   = findViewById(R.id.etFullName)
        etEmail      = findViewById(R.id.etEmail)
        etPhone      = findViewById(R.id.etPhone)
        etBio        = findViewById(R.id.etBio)
        etAge        = findViewById(R.id.etAge)
        etHeight     = findViewById(R.id.etHeight)
        etWeight     = findViewById(R.id.etWeight)
        etCalories   = findViewById(R.id.etCalories)
        etConditions = findViewById(R.id.etConditions)
        spinnerGender    = findViewById(R.id.spinnerGender)
        spinnerBloodType = findViewById(R.id.spinnerBloodType)
        spinnerActivity  = findViewById(R.id.spinnerActivity)
        btnSave      = findViewById(R.id.btnSave)
        btnBack      = findViewById(R.id.btnBack)
        btnChangePic = findViewById(R.id.btnChangePic)
        imgProfile   = findViewById(R.id.imgProfile)
        cardProfile  = findViewById(R.id.cardProfilePic)

        // Setup spinners
        setupSpinners()

        // Load saved data
        loadSavedData()

        // Back button
        btnBack.setOnClickListener { finish() }

        // Change photo
        btnChangePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Save button
        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun setupSpinners() {
        // Gender spinner
        val genders = arrayOf("Gender", "Male", "Female", "Other")
        spinnerGender.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, genders)

        // Blood type spinner
        val bloodTypes = arrayOf("Blood Type", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        spinnerBloodType.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, bloodTypes)

        // Activity level spinner
        val activityLevels = arrayOf("Activity Level",
            "Sedentary", "Lightly active",
            "Moderately active", "Very active", "Extremely active")
        spinnerActivity.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, activityLevels)
    }

    private fun loadSavedData() {
        // Load from Firebase
        val user = auth.currentUser
        etFullName.setText(user?.displayName ?: "")
        etEmail.setText(user?.email ?: "")

        // Load health data from SharedPreferences
        etPhone.setText(sharedPref.getString("phone", ""))
        etBio.setText(sharedPref.getString("bio", ""))
        etAge.setText(sharedPref.getString("age", ""))
        etHeight.setText(sharedPref.getString("height", ""))
        etWeight.setText(sharedPref.getString("weight", ""))
        etCalories.setText(sharedPref.getString("calories", ""))
        etConditions.setText(sharedPref.getString("conditions", ""))

        // Restore spinner selections
        val genderPos = sharedPref.getInt("gender_pos", 0)
        val bloodPos  = sharedPref.getInt("blood_pos", 0)
        val actPos    = sharedPref.getInt("activity_pos", 0)
        spinnerGender.setSelection(genderPos)
        spinnerBloodType.setSelection(bloodPos)
        spinnerActivity.setSelection(actPos)
    }

    private fun saveProfile() {
        val name       = etFullName.text.toString().trim()
        val age        = etAge.text.toString().trim()
        val height     = etHeight.text.toString().trim()
        val weight     = etWeight.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            etFullName.error = "Name is required"
            etFullName.requestFocus()
            return
        }
        if (age.isNotEmpty() && age.toIntOrNull() == null) {
            etAge.error = "Enter a valid age"
            return
        }
        if (height.isNotEmpty() && height.toFloatOrNull() == null) {
            etHeight.error = "Enter valid height"
            return
        }
        if (weight.isNotEmpty() && weight.toFloatOrNull() == null) {
            etWeight.error = "Enter valid weight"
            return
        }

        // Save name to Firebase
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        auth.currentUser?.updateProfile(profileUpdates)

        // Save all health data to SharedPreferences
        with(sharedPref.edit()) {
            putString("user_name",   name)
            putString("phone",       etPhone.text.toString().trim())
            putString("bio",         etBio.text.toString().trim())
            putString("age",         age)
            putString("height",      height)
            putString("weight",      weight)
            putString("calories",    etCalories.text.toString().trim())
            putString("conditions",  etConditions.text.toString().trim())
            putInt("gender_pos",     spinnerGender.selectedItemPosition)
            putInt("blood_pos",      spinnerBloodType.selectedItemPosition)
            putInt("activity_pos",   spinnerActivity.selectedItemPosition)
            apply()
        }

        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imgProfile.setImageURI(imageUri)
            // Save URI to SharedPreferences
            sharedPref.edit().putString("profile_pic_uri", imageUri.toString()).apply()
        }
    }
}