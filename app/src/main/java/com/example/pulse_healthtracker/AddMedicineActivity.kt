package com.example.pulse_healthtracker

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
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

    private var editingMedicineId: String? = null
    private var currentIsTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_medicine)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initializeFirebase()
        setupClickListeners()

        val medicineId = intent.getStringExtra("medicineId")
        if (medicineId != null) {
            editingMedicineId = medicineId
            loadMedicineData(medicineId)
        }
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
        btnBack.setOnClickListener { finish() }
        etTime.setOnClickListener { showTimePickerDialog() }
        spFoodRelation.setOnClickListener { showFoodRelationDialog() }
        btnAddTimes.setOnClickListener { showAddTimeDialog() }
        btnSubmitMedicine.setOnClickListener { submitMedicine() }
        cvPillImage.setOnClickListener { showImagePickerDialog() }
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun loadMedicineData(id: String) {
        tvHeaderTitle.text = "Edit Medicine"
        btnSubmitMedicine.text = "Update Medicine"
        
        firestore.collection("medications").document(id).get()
            .addOnSuccessListener { doc ->
                val med = doc.toObject(Medicine::class.java)
                if (med != null) {
                    etPillName.setText(med.pillName)
                    spDiseases.setText(med.diseases)
                    etDose.setText(med.dose.toString())
                    etTime.text = med.time
                    selectedTime = med.time
                    spFoodRelation.text = med.foodRelation
                    selectedFoodRelation = med.foodRelation
                    etNotes.setText(med.notes)
                    medicineTimes.clear()
                    medicineTimes.addAll(med.medicineTimes)
                    currentIsTaken = med.isTaken
                }
            }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
            val formattedTime = formatTime(hour, minute)
            selectedTime = formattedTime
            etTime.text = formattedTime
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
    }

    private fun showFoodRelationDialog() {
        val options = arrayOf("Before eat", "After eat", "With food", "Empty stomach")
        AlertDialog.Builder(this).setTitle("Select Timing").setItems(options) { _, which ->
            selectedFoodRelation = options[which]
            spFoodRelation.text = selectedFoodRelation
        }.show()
    }

    private fun showAddTimeDialog() {
        val timeList = arrayOf("Morning", "Afternoon", "Evening", "Night", "Custom")
        AlertDialog.Builder(this).setTitle("Add Time").setItems(timeList) { _, which ->
            if (which == 4) showCustomTimeDialog() else addMedicineTime(timeList[which])
        }.show()
    }

    private fun showCustomTimeDialog() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            addMedicineTime(formatTime(hour, minute))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
    }

    private fun addMedicineTime(time: String) {
        if (!medicineTimes.contains(time)) {
            medicineTimes.add(time)
            Toast.makeText(this, "Added: $time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImagePickerDialog() {
        Toast.makeText(this, "Image picker coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun submitMedicine() {
        val pillName = etPillName.text.toString().trim()
        val diseases = spDiseases.text.toString().trim()
        val doseStr = etDose.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        if (pillName.isEmpty() || diseases.isEmpty() || doseStr.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val dose = doseStr.toDoubleOrNull() ?: 0.0
        val userId = auth.currentUser?.uid ?: return

        btnSubmitMedicine.isEnabled = false
        btnSubmitMedicine.text = if (editingMedicineId == null) "Adding..." else "Updating..."

        val docRef = if (editingMedicineId == null) {
            firestore.collection("medications").document()
        } else {
            firestore.collection("medications").document(editingMedicineId!!)
        }

        val medicineData = hashMapOf(
            "medicineId" to docRef.id,
            "pillName" to pillName,
            "diseases" to diseases,
            "dose" to dose,
            "time" to selectedTime,
            "foodRelation" to selectedFoodRelation,
            "notes" to notes,
            "medicineTimes" to medicineTimes,
            "createdAt" to System.currentTimeMillis(),
            "status" to "active",
            "userId" to userId,
            "isTaken" to currentIsTaken
        )

        // Optimistic UI: Close immediately and let Firestore handle sync in background
        docRef.set(medicineData)
            .addOnFailureListener { e ->
                // If it fails after we already closed, we can't easily undo the finish()
                // but this is extremely rare with Firestore's offline capabilities.
                android.util.Log.e("AddMedicine", "Background upload failed", e)
            }

        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show()
        finish()
    }
}
