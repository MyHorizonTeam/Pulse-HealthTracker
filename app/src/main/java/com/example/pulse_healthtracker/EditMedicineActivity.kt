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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class EditMedicineActivity : AppCompatActivity() {

    private lateinit var etPillName: EditText
    private lateinit var spDiseases: EditText
    private lateinit var etDate: TextView
    private lateinit var etDose1: EditText
    private lateinit var etTime1: TextView
    private lateinit var spFoodRelation1: TextView
    private lateinit var etDose2: EditText
    private lateinit var etTime2: TextView
    private lateinit var spFoodRelation2: TextView
    private lateinit var btnAddTimes: LinearLayout
    private lateinit var etNotes: EditText
    private lateinit var btnSave: androidx.appcompat.widget.AppCompatButton
    private lateinit var btnRemove: androidx.appcompat.widget.AppCompatButton
    private lateinit var ivMedicineImage: ImageView
    private lateinit var cvPillImage: CardView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var medicineId: String? = null
    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String = ""
    private val medicineTimes = mutableListOf<String>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            ivMedicineImage.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_medicine)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        medicineId = intent.getStringExtra("medicineId")

        if (medicineId != null) {
            loadMedicineData(medicineId!!)
        } else {
            Toast.makeText(this, "Error: No medicine ID found", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupClickListeners()
    }

    private fun initViews() {
        etPillName = findViewById(R.id.etPillName)
        spDiseases = findViewById(R.id.spDiseases)
        etDate = findViewById(R.id.etDate)
        etDose1 = findViewById(R.id.etDose1)
        etTime1 = findViewById(R.id.etTime1)
        spFoodRelation1 = findViewById(R.id.spFoodRelation1)
        etDose2 = findViewById(R.id.etDose2)
        etTime2 = findViewById(R.id.etTime2)
        spFoodRelation2 = findViewById(R.id.spFoodRelation2)
        btnAddTimes = findViewById(R.id.btnAddTimes)
        etNotes = findViewById(R.id.etNotes)
        btnSave = findViewById(R.id.btnSaveMedicine)
        btnRemove = findViewById(R.id.btnRemoveMedicine)
        ivMedicineImage = findViewById(R.id.ivMedicineImage)
        cvPillImage = findViewById(R.id.cvPillImage)
        
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        etDate.setOnClickListener { showDatePickerDialog() }
        etTime1.setOnClickListener { showTimePickerDialog(etTime1) }
        etTime2.setOnClickListener { showTimePickerDialog(etTime2) }
        
        spFoodRelation1.setOnClickListener { showFoodRelationDialog(spFoodRelation1) }
        spFoodRelation2.setOnClickListener { showFoodRelationDialog(spFoodRelation2) }
        
        btnSave.setOnClickListener { saveMedicine() }
        btnRemove.setOnClickListener { removeMedicine() }
        cvPillImage.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnAddTimes.setOnClickListener { showAddTimeDialog() }
    }

    private fun loadMedicineData(id: String) {
        firestore.collection("medications").document(id).get()
            .addOnSuccessListener { doc ->
                val med = doc.toObject(Medicine::class.java)
                if (med != null) {
                    etPillName.setText(med.pillName)
                    spDiseases.setText(med.diseases)
                    etDate.text = med.date
                    etDose1.setText(med.dose.toString())
                    etTime1.text = med.time
                    spFoodRelation1.text = med.foodRelation
                    etNotes.setText(med.notes)
                    currentImageUrl = med.imageUrl
                    medicineTimes.clear()
                    medicineTimes.addAll(med.medicineTimes)

                    if (currentImageUrl.isNotEmpty()) {
                        Glide.with(this).load(currentImageUrl).into(ivMedicineImage)
                    }
                    
                    if (med.medicineTimes.isNotEmpty()) {
                        etTime2.text = med.medicineTimes[0]
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showTimePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                textView.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            false,
        ).show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        android.app.DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etDate.text = format.format(cal.time)
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH],
        ).show()
    }

    private fun showFoodRelationDialog(textView: TextView) {
        val options = arrayOf("Before eat", "After eat", "With food", "Empty stomach")
        AlertDialog.Builder(this).setTitle("Select Timing").setItems(options) { _, which ->
            textView.text = options[which]
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
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                addMedicineTime(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time))
            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            false,
        ).show()
    }

    private fun addMedicineTime(time: String) {
        if (!medicineTimes.contains(time)) {
            medicineTimes.add(time)
            // Update etTime2 with the latest added time if it's the first extra time
            if (medicineTimes.size == 1) {
                etTime2.text = time
            }
            Toast.makeText(this, "Added: $time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveMedicine() {
        val pillName = etPillName.text.toString().trim()
        val diseases = spDiseases.text.toString().trim()
        val dose1 = etDose1.text.toString().toDoubleOrNull() ?: 0.0
        val time1 = etTime1.text.toString()
        val food1 = spFoodRelation1.text.toString()
        val notes = etNotes.text.toString().trim()
        val date = etDate.text.toString()

        if (pillName.isEmpty() || diseases.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        btnSave.isEnabled = false
        btnSave.text = getString(R.string.loading)

        if (selectedImageUri != null) {
            performBackgroundUploadAndSave(pillName, diseases, dose1, time1, food1, notes, date)
        } else {
            performBackgroundUpdate(pillName, diseases, dose1, time1, food1, notes, date, currentImageUrl)
        }

        // Optimistic UI: Close immediately
        Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun performBackgroundUploadAndSave(pillName: String, diseases: String, dose: Double, time: String, food: String, notes: String, date: String) {
        val ref = storage.reference.child("medicine_images/${UUID.randomUUID()}")
        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    performBackgroundUpdate(pillName, diseases, dose, time, food, notes, date, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("EditMedicine", "Image upload failed", e)
                performBackgroundUpdate(pillName, diseases, dose, time, food, notes, date, currentImageUrl)
            }
    }

    private fun performBackgroundUpdate(pillName: String, diseases: String, dose: Double, time: String, food: String, notes: String, date: String, imageUrl: String) {
        val updateData = hashMapOf<String, Any>(
            "pillName" to pillName,
            "diseases" to diseases,
            "date" to date,
            "dose" to dose,
            "time" to time,
            "foodRelation" to food,
            "notes" to notes,
            "imageUrl" to imageUrl,
            "medicineTimes" to medicineTimes,
        )

        firestore.collection("medications").document(medicineId!!)
            .update(updateData)
            .addOnFailureListener { e ->
                android.util.Log.e("EditMedicine", "Update failed in background", e)
            }
    }

    private fun removeMedicine() {
        AlertDialog.Builder(this)
            .setTitle("Remove Medicine")
            .setMessage("Are you sure you want to remove this medicine?")
            .setPositiveButton("Remove") { _, _ ->
                firestore.collection("medications").document(medicineId!!).delete()
                    .addOnFailureListener { e ->
                        android.util.Log.e("EditMedicine", "Delete failed in background", e)
                    }

                Toast.makeText(this, "Removing...", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
