package com.example.pulse_healthtracker

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddMedicineActivity : AppCompatActivity() {
    private lateinit var btnBack: TextView
    private lateinit var tvHeaderTitle: TextView
    private lateinit var cvPillImage: CardView
    private lateinit var etPillName: EditText
    private lateinit var spDiseases: EditText
    private lateinit var etDose: EditText
    private lateinit var etTime: TextView
    private lateinit var spFoodRelation: TextView
    private lateinit var btnAddTimes: LinearLayout
    private lateinit var etNotes: EditText
    private lateinit var btnSubmitMedicine: androidx.appcompat.widget.AppCompatButton

    // Data variables
    private var selectedTime = "08:00 AM"
    private var selectedFoodRelation = "Before eat"
    private val medicineTimes = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        initViews()
        setupClickListeners()
        initializeFirebase()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        cvPillImage = findViewById(R.id.cvPillImage)
        etPillName = findViewById(R.id.etPillName)
        spDiseases = findViewById(R.id.spDiseases)
        etDose = findViewById(R.id.etDose)
        etTime = findViewById(R.id.etTime)
        spFoodRelation = findViewById(R.id.spFoodRelation)
        btnAddTimes = findViewById(R.id.btnAddTimes)
        etNotes = findViewById(R.id.etNotes)
        btnSubmitMedicine = findViewById(R.id.btnSubmitMedicine)
    }

    private fun setupClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Time picker
        etTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Food relation spinner (using TextView as dropdown)
        spFoodRelation.setOnClickListener {
            showFoodRelationDialog()
        }

        // Add times button
        btnAddTimes.setOnClickListener {
            showAddTimeDialog()
        }

        // Submit button
        btnSubmitMedicine.setOnClickListener {
            submitMedicine()
        }

        // Profile image click (optional - for changing image)
        cvPillImage.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val formattedTime = formatTime(selectedHour, selectedMinute)
                selectedTime = formattedTime
                etTime.text = formattedTime
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun showFoodRelationDialog() {
        val options = arrayOf("Before eat", "After eat", "With food", "Empty stomach")

        AlertDialog.Builder(this)
            .setTitle("Select when to take")
            .setItems(options) { _, which ->
                selectedFoodRelation = options[which]
                spFoodRelation.text = selectedFoodRelation
            }
            .show()
    }

    private fun showAddTimeDialog() {
        val timeList = arrayOf("Morning (6AM - 9AM)", "Afternoon (12PM - 2PM)",
            "Evening (5PM - 7PM)", "Night (9PM - 10PM)", "Custom")

        AlertDialog.Builder(this)
            .setTitle("Add Medicine Time")
            .setItems(timeList) { _, which ->
                when (which) {
                    0 -> addMedicineTime("Morning (6AM - 9AM)")
                    1 -> addMedicineTime("Afternoon (12PM - 2PM)")
                    2 -> addMedicineTime("Evening (5PM - 7PM)")
                    3 -> addMedicineTime("Night (9PM - 10PM)")
                    4 -> showCustomTimeDialog()
                }
            }
            .show()
    }

    private fun showCustomTimeDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                val customTime = formatTime(hour, minute)
                addMedicineTime(customTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun addMedicineTime(time: String) {
        if (!medicineTimes.contains(time)) {
            medicineTimes.add(time)
            Toast.makeText(this, "Added: $time", Toast.LENGTH_SHORT).show()

            // Optional: Display all added times
            displayAddedTimes()
        } else {
            Toast.makeText(this, "Time already added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayAddedTimes() {
        if (medicineTimes.isNotEmpty()) {
            val timesText = medicineTimes.joinToString(", ")
            Toast.makeText(this, "Times: $timesText", Toast.LENGTH_LONG).show()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(this)
            .setTitle("Select Pill Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        // Implement camera intent
        Toast.makeText(this, "Camera feature coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        // Implement gallery intent
        Toast.makeText(this, "Gallery feature coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun submitMedicine() {
        val pillName = etPillName.text.toString().trim()
        val diseases = spDiseases.text.toString().trim()
        val dose = etDose.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        // Validation
        when {
            pillName.isEmpty() -> {
                etPillName.error = "Pill name is required"
                etPillName.requestFocus()
                return
            }
            diseases.isEmpty() -> {
                spDiseases.error = "Disease is required"
                spDiseases.requestFocus()
                return
            }
            dose.isEmpty() -> {
                etDose.error = "Dose is required"
                etDose.requestFocus()
                return
            }
            dose.toDoubleOrNull() == null -> {
                etDose.error = "Enter valid dose"
                etDose.requestFocus()
                return
            }
        }

        // Disable button during submission
        btnSubmitMedicine.isEnabled = false
        btnSubmitMedicine.text = "Adding..."

        // Prepare data for Firebase
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            btnSubmitMedicine.isEnabled = true
            btnSubmitMedicine.text = "Add Medicine"
            return
        }

        val medicineData = hashMapOf(
            "pillName" to pillName,
            "diseases" to diseases,
            "dose" to dose.toDouble(),
            "time" to selectedTime,
            "foodRelation" to selectedFoodRelation,
            "notes" to notes,
            "medicineTimes" to medicineTimes,
            "createdAt" to System.currentTimeMillis(),
            "status" to "active",
            "userId" to userId
        )

        // Save to Firestore
        firestore.collection("medications")
            .add(medicineData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Medicine added successfully!", Toast.LENGTH_LONG).show()

                // Optionally update the document with its own ID
                documentReference.update("medicineId", documentReference.id)
                    .addOnSuccessListener {
                        finish() // Close activity on success
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnSubmitMedicine.isEnabled = true
                btnSubmitMedicine.text = "Add Medicine"
            }
    }

    // Optional: For editing existing medicine
    fun populateDataForEdit(medicine: Medicine) {
        etPillName.setText(medicine.pillName)
        spDiseases.setText(medicine.diseases)
        etDose.setText(medicine.dose.toString())
        etTime.text = medicine.time
        spFoodRelation.text = medicine.foodRelation
        etNotes.setText(medicine.notes)
        medicineTimes.clear()
        medicineTimes.addAll(medicine.medicineTimes)
        btnSubmitMedicine.text = "Update Medicine"
    }
}

// Data class for Medicine
data class Medicine(
    val medicineId: String = "",
    val pillName: String = "",
    val diseases: String = "",
    val dose: Double = 0.0,
    val time: String = "",
    val foodRelation: String = "",
    val notes: String = "",
    val medicineTimes: List<String> = emptyList(),
    val createdAt: Long = 0,
    val status: String = "",
    val userId: String = ""
)