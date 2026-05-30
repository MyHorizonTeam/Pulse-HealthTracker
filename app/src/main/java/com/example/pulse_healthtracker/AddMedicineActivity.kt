package com.example.pulse_healthtracker

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddMedicineActivity : AppCompatActivity() {
    private lateinit var btnBack: TextView
    private lateinit var tvHeaderTitle: TextView
    private lateinit var cvPillImage: CardView
    private lateinit var ivMedicineImage: ImageView
    private lateinit var etPillName: EditText
    private lateinit var spDiseases: EditText
    private lateinit var etDate: TextView
    private lateinit var etDose: EditText
    private lateinit var etTime: TextView
    private lateinit var spFoodRelation: TextView
    private lateinit var btnAddTimes: LinearLayout
    private lateinit var etNotes: EditText
    private lateinit var btnSubmitMedicine: androidx.appcompat.widget.AppCompatButton

    // Data variables
    private var selectedDate = ""
    private var selectedTime = "08:00 AM"
    private var selectedFoodRelation = "Before eat"
    private val medicineTimes = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var editingMedicineId: String? = null
    private var currentIsTaken = false
    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String = ""

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            ivMedicineImage.setImageURI(uri)
        }
    }

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
        val preSelectedDate = intent.getStringExtra("selectedDate")
        
        if (medicineId != null) {
            editingMedicineId = medicineId
            loadMedicineData(medicineId)
        } else if (preSelectedDate != null) {
            selectedDate = preSelectedDate
            etDate.text = selectedDate
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        cvPillImage = findViewById(R.id.cvPillImage)
        ivMedicineImage = findViewById(R.id.ivMedicineImage)
        etPillName = findViewById(R.id.etPillName)
        spDiseases = findViewById(R.id.spDiseases)
        etDate = findViewById(R.id.etDate)
        etDose = findViewById(R.id.etDose)
        etTime = findViewById(R.id.etTime)
        spFoodRelation = findViewById(R.id.spFoodRelation)
        btnAddTimes = findViewById(R.id.btnAddTimes)
        etNotes = findViewById(R.id.etNotes)
        btnSubmitMedicine = findViewById(R.id.btnSubmitMedicine)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        etDate.setOnClickListener { showDatePickerDialog() }
        etTime.setOnClickListener { showTimePickerDialog() }
        spFoodRelation.setOnClickListener { showFoodRelationDialog() }
        btnAddTimes.setOnClickListener { showAddTimeDialog() }
        btnSubmitMedicine.setOnClickListener { submitMedicine() }
        cvPillImage.setOnClickListener { showImagePickerDialog() }
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    private fun loadMedicineData(id: String) {
        tvHeaderTitle.text = getString(R.string.edit_medicine)
        btnSubmitMedicine.text = getString(R.string.update_medicine)
        
        firestore.collection("medications").document(id).get()
            .addOnSuccessListener { doc ->
                val med = doc.toObject(Medicine::class.java)
                if (med != null) {
                    etPillName.setText(med.pillName)
                    spDiseases.setText(med.diseases)
                    etDose.setText(med.dose.toString())
                    etTime.text = med.time
                    selectedTime = med.time
                    etDate.text = med.date
                    selectedDate = med.date
                    spFoodRelation.text = med.foodRelation
                    selectedFoodRelation = med.foodRelation
                    etNotes.setText(med.notes)
                    medicineTimes.clear()
                    medicineTimes.addAll(med.medicineTimes)
                    currentIsTaken = med.isTaken
                    currentImageUrl = med.imageUrl
                    if (currentImageUrl.isNotEmpty()) {
                        Glide.with(this).load(currentImageUrl).into(ivMedicineImage)
                    }
                }
            }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                val formattedTime = formatTime(hour, minute)
                selectedTime = formattedTime
                etTime.text = formattedTime
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            false,
        )
        timePickerDialog.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = format.format(cal.time)
                etDate.text = selectedDate
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
        )
        datePickerDialog.show()
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
        TimePickerDialog(
            this,
            { _, hour, minute ->
                addMedicineTime(formatTime(hour, minute))
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            false,
        ).show()
    }

    private fun addMedicineTime(time: String) {
        if (!medicineTimes.contains(time)) {
            medicineTimes.add(time)
            Toast.makeText(this, "Added: $time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImagePickerDialog() {
        pickImageLauncher.launch("image/*")
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

        if (selectedImageUri != null) {
            uploadImageAndSubmit(pillName, diseases, dose, notes, userId)
        } else {
            saveToFirestore(pillName, diseases, dose, notes, userId, currentImageUrl)
        }
    }

    private fun uploadImageAndSubmit(pillName: String, diseases: String, dose: Double, notes: String, userId: String) {
        val ref = storage.reference.child("medicine_images/${UUID.randomUUID()}")
        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveToFirestore(pillName, diseases, dose, notes, userId, uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                saveToFirestore(pillName, diseases, dose, notes, userId, currentImageUrl)
            }
    }

    private fun saveToFirestore(pillName: String, diseases: String, dose: Double, notes: String, userId: String, imageUrl: String) {
        val docRef = if (editingMedicineId == null) {
            firestore.collection("medications").document()
        } else {
            firestore.collection("medications").document(editingMedicineId!!)
        }

        // Default to today if no date selected
        if (selectedDate.isEmpty()) {
            val cal = Calendar.getInstance()
            selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
        }

        val medicineData = hashMapOf(
            "medicineId" to docRef.id,
            "pillName" to pillName,
            "diseases" to diseases,
            "dose" to dose,
            "date" to selectedDate,
            "time" to selectedTime,
            "foodRelation" to selectedFoodRelation,
            "notes" to notes,
            "medicineTimes" to medicineTimes,
            "createdAt" to System.currentTimeMillis(),
            "status" to "active",
            "userId" to userId,
            "isTaken" to currentIsTaken,
            "imageUrl" to imageUrl,
        )

        // Optimistic UI: Trigger save and close screen immediately
        docRef.set(medicineData)
            .addOnFailureListener { e ->
                android.util.Log.e("AddMedicine", "Background save failed", e)
            }

        Toast.makeText(this, "Saving medicine...", Toast.LENGTH_SHORT).show()
        finish()
    }
}
